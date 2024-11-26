package com.nembx.mypan.model.dto.file;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lian
 */

@Data
public class FileAddRequest implements Serializable {

    private MultipartFile file;

    private int chunkNumber;

    private String fileType;

    private String fileName;

    private String fileMd5;

    private String filePath;

    @Serial
    private static final long serialVersionUID = 1L;
}
