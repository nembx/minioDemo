package com.nembx.mypan.model.dto.file;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lian
 */

@Data
public class FileDeleteRequest implements Serializable {

    private Long id;

    private String fileName;

    private String fileMd5;

    @Serial
    private static final long serialVersionUID = 1L;
}
