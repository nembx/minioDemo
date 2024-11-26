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
@TableName("user")
public class User implements Serializable {

    @TableId
    private Long id;

    private String userName;

    private String password;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
