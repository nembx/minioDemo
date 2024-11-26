package com.nembx.mypan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nembx.mypan.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Lian
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
