package com.nembx.mypan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nembx.mypan.mapper.UserMapper;
import com.nembx.mypan.model.entity.User;
import com.nembx.mypan.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author Lian
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
