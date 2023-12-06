package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Override
    public User checkOrSave(String phone) {
        //查看用户是否已注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = this.getOne(queryWrapper);
        if (user == null){
            //如果当前手机号是否为新用户自动完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            this.save(user);
        }
        return user;
    }
}
