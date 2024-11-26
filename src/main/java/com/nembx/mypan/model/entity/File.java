package com.nembx.mypan.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lian
 */
@Data
@TableName("files")
public class File implements Serializable {

    @TableId
    private Long id;

    private String fileName;

    private String fileMd5;

    private String fileType;

    private String uploadTime;

    private String filePath;

    @TableField(exist = false)
    @Serial
    private static final long serialVersionUID = 1L;
}
