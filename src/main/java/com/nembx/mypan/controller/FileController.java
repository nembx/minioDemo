package com.nembx.mypan.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nembx.mypan.common.BaseResponse;
import com.nembx.mypan.common.ErrorCode;
import com.nembx.mypan.model.dto.file.FileAddRequest;
import com.nembx.mypan.model.dto.file.FileDeleteRequest;
import com.nembx.mypan.model.dto.file.FileQueryRequest;
import com.nembx.mypan.model.entity.File;
import com.nembx.mypan.service.FileService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * @author Lian
 */

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;


    @PostMapping("/upload/{chunkNumber}")
    public BaseResponse<Boolean> uploadFile(@RequestParam("file") MultipartFile file,
                                            @PathVariable("chunkNumber") int chunkNumber,
                                            @RequestParam("fileName") String fileName,
                                            @RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("fileSize") int fileSize,
                                            @RequestParam("fileType") String fileType){
        FileAddRequest fileAddRequest = new FileAddRequest();
        fileAddRequest.setFile(file);
        fileAddRequest.setChunkNumber(chunkNumber);
        fileAddRequest.setFileName(fileName);
        fileAddRequest.setFileMd5(fileMd5);
        fileAddRequest.setFileType(fileType);
        if(fileSize < 1024 * 1024 * 5){
            boolean small = fileService.smallFile(fileAddRequest);
            if (!small){
                return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
            }
            return new BaseResponse<>(200,"上传成功");
        }
        boolean result = fileService.saveFile(fileAddRequest);
        if (!result){
            return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
        }
        return new BaseResponse<>(200, "上传成功");
    }

    @PostMapping("/merge")
    public BaseResponse<Boolean> mergeFile(@RequestParam("fileName") String fileName,
                                           @RequestParam("fileType") String fileType,
                                           @RequestParam("fileMd5") String fileMd5,
                                           @RequestParam("fileSize") int fileSize){
        if (fileSize < 1024 * 1024 * 5){
            return new BaseResponse<>(200, "上传成功");
        }
        FileQueryRequest fileQueryRequest = new FileQueryRequest();
        fileQueryRequest.setFileName(fileName);
        fileQueryRequest.setFileType(fileType);
        fileQueryRequest.setFileMd5(fileMd5);
        boolean result = fileService.mergeFile(fileQueryRequest);
        if (!result){
            return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
        }
        return new BaseResponse<>(200, "上传成功");
    }

    @GetMapping("/download")
    public void downloadFile(@RequestBody FileQueryRequest fileQueryRequest){
        fileService.downloadFile(fileQueryRequest);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteFile(@RequestBody FileDeleteRequest fileDeleteRequest){
        boolean result = fileService.removeFile(fileDeleteRequest);
        if (!result){
            return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
        }
        return new BaseResponse<>(200, "删除成功");
    }

    @GetMapping("/list")
    public BaseResponse<Page<File>> fileList(){
//        List<File> fileList = fileService.list();
        Page<File> filePage = fileService.page(new Page<>(1, 10));
        if (filePage == null){
            return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
        }
        return new BaseResponse<>(200, filePage, "查询成功");
    }
}
