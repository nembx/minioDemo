package com.nembx.mypan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nembx.mypan.model.dto.file.FileAddRequest;
import com.nembx.mypan.model.dto.file.FileDeleteRequest;
import com.nembx.mypan.model.dto.file.FileQueryRequest;
import com.nembx.mypan.model.entity.File;

/**
 * @author Lian
 */
public interface FileService extends IService<File> {

    boolean saveFile(FileAddRequest fileAddRequest);

    boolean mergeFile(FileQueryRequest fileQueryRequest);

    void downloadFile(FileQueryRequest fileQueryRequest);

    boolean removeFile(FileDeleteRequest fileDeleteRequest);

    boolean smallFile(FileAddRequest fileAddRequest);
}
