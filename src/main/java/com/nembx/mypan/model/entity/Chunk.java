package com.nembx.mypan.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Lian
 */

@Data
@TableName("chunk")
public class Chunk implements Serializable {
    @TableId
    private Long chunkId;

    private String chunkName;

    private String chunkMd5;

    private String chunkType;
}
