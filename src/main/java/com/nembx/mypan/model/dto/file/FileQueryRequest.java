package com.nembx.mypan.model.dto.file;

import com.nembx.mypan.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lian
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class FileQueryRequest extends PageRequest implements Serializable {

    private Long id;

    private String fileName;

    private String fileMd5;

    private String fileType;

    @Serial
    private static final long serialVersionUID = 1L;
}
