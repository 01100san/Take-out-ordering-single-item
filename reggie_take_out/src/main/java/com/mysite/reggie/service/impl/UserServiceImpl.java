package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.entity.User;
import com.mysite.reggie.mapper.UserMapper;
import com.mysite.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * ClassName: UserServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/28 18:59
 * version 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
