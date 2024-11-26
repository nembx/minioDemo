package com.nembx.mypan.service.impl;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nembx.mypan.mapper.ChunkMapper;
import com.nembx.mypan.mapper.FileMapper;
import com.nembx.mypan.model.dto.file.FileAddRequest;
import com.nembx.mypan.model.dto.file.FileDeleteRequest;
import com.nembx.mypan.model.dto.file.FileQueryRequest;
import com.nembx.mypan.model.entity.Chunk;
import com.nembx.mypan.model.entity.File;
import com.nembx.mypan.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.Resource;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author Lian
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class FilServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Resource
    private MinioClient minioClient;

    @Resource
    private FileMapper fileMapper;

    @Resource
    private ChunkMapper chunkMapper;

    private final Map<Integer,Boolean> map = new HashMap<>();


    @Override
    public boolean saveFile(FileAddRequest fileAddRequest) {
        MultipartFile file = fileAddRequest.getFile();
        String fileName = fileAddRequest.getFileName();
        String fileMd5 = fileAddRequest.getFileMd5();
        String fileType = fileAddRequest.getFileType();
        int chunkNumber = fileAddRequest.getChunkNumber();

        Chunk chunk = new Chunk();
        chunk.setChunkMd5(fileMd5);
        chunk.setChunkType(fileType);
        chunk.setChunkName(fileName + "_" + chunkNumber + "分片");
        chunkMapper.insert(chunk);

        if (file.isEmpty()){
            throw new RuntimeException("文件为空");
        }
        if (map.containsKey(chunkNumber)){
            throw new RuntimeException("文件已存在");
        }
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("test")
                    .object(chunkNumber + "_"+ fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .build());
            map.put(chunkNumber,true);
            inputStream.close();
        }catch (Exception e){
            throw new RuntimeException("文件上传失败");
        }
        return true;
    }

    @Override
    public boolean mergeFile(FileQueryRequest fileQueryRequest) {
        String fileName = fileQueryRequest.getFileName();
        String fileType = fileQueryRequest.getFileType();
        String bucket = "test";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < map.size(); i++) {
                GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(i + "_" + fileName)
                        .build();
                InputStream stream = minioClient.getObject(getObjectArgs);
                IOUtils.copy(stream, outputStream);
                stream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("获取分块失败", e);
        }

        byte[] mergedBytes = outputStream.toByteArray();

        String mergedFileMd5 = DigestUtils.md5DigestAsHex(mergedBytes);

        String fileMd5 = fileQueryRequest.getFileMd5();
        if (!mergedFileMd5.equals(fileMd5)) {
            throw new RuntimeException("文件 MD5 校验失败，前端 MD5: " + fileMd5 + ", 后端 MD5: " + mergedFileMd5);
        }

        List<ComposeSource> sources = new ArrayList<>();
        for (int i = 0; i < map.size(); i++){
            ComposeSource chunk = ComposeSource
                    .builder()
                    .bucket(bucket)
                    .object(i + "_"+ fileName)
                    .build();
            sources.add(chunk);
        }
        try{
            if("video/mp4".equals(fileType)) {
                minioClient.composeObject(ComposeObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(fileName)
                        .sources(sources)
                        .build());
            }
            for (int i = 0; i < map.size(); i++){
                minioClient.removeObject(RemoveObjectArgs
                        .builder()
                        .bucket(bucket)
                        .object(i + "_"+ fileName)
                        .build());
            }
        }catch (Exception e){
            throw new RuntimeException("系统出错");
        }
        map.clear();
        File file = new File();
        file.setFileName(fileName);
        file.setFileType(fileType);
        file.setFilePath(bucket);
        file.setFileMd5(fileMd5);
        fileMapper.insert(file);
        return true;
    }


    @Override
    public void downloadFile(FileQueryRequest fileQueryRequest) {
        String fileName = fileQueryRequest.getFileName();
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket("test")
                            .object(fileName)
                            .build());
            byte[] b = new byte[1024];
            int len = 0;
            FastByteArrayOutputStream fbaos = new FastByteArrayOutputStream();
            while ((len = response.read(b)) != -1){
                fbaos.write(b,0,len);
            }
            fbaos.close();
            response.close();
        }catch (Exception e){
            throw new RuntimeException("文件下载失败");
        }
    }

    @Override
    public boolean removeFile(FileDeleteRequest fileDeleteRequest) {
        String fileName = fileDeleteRequest.getFileName();
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket("test")
                            .object(fileName)
                            .build());
        }catch (Exception e){
            throw new RuntimeException("文件删除失败");
        }
        return true;
    }

    @Override
    public boolean smallFile(FileAddRequest fileAddRequest) {
        String fileName = fileAddRequest.getFileName();
        String fileType = fileAddRequest.getFileType();
        MultipartFile file = fileAddRequest.getFile();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs
                    .builder()
                    .bucket("test")
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .build());
            inputStream.close();
            File fileData = new File();
            fileData.setFileName(fileName);
            fileData.setFileType(fileType);
            fileData.setFilePath("test");
            fileData.setFileMd5(DigestUtils.md5DigestAsHex(file.getBytes()));
            fileMapper.insert(fileData);
        }catch (Exception e){
            throw new RuntimeException("文件上传失败");
        }
        return true;
    }



    public void findFileImf(String fileName){
        try {
            StatObjectResponse status = minioClient.statObject(StatObjectArgs
                    .builder()
                    .bucket("test")
                    .object(fileName)
                    .build());
            File file = new File();
            String etag = status.etag();
            String object = status.object();
            String contentType = status.contentType();
            String bucket = status.bucket();
            file.setFileMd5(etag);
            file.setFileName(object);
            file.setFileType(contentType);
            file.setFilePath(bucket);
            fileMapper.insert(file);
        }catch (ErrorResponseException | InsufficientDataException |
                InternalException |
                InvalidKeyException | InvalidResponseException |
                IOException | NoSuchAlgorithmException | ServerException |
                XmlParserException e){
            log.error("{}" + e);
        }
    }
}
