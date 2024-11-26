package com.nembx.mypan.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Lian
 */
@Data
public class UserQueryRequest implements Serializable {
    private Long id;

    private String userName;

    @Serial
    private static final long serialVersionUID = 1L;
}
