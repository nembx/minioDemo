package com.nembx.mypan.common;

import lombok.Data;

/**
 * @author Lian
 */

@Data
public class PageRequest {

    private int pageNum = 1;

    private int pageSize = 10;

}
