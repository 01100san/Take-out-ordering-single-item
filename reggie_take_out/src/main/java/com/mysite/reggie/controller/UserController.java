package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.User;
import com.mysite.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.awt.print.PrinterJob;
import java.util.Map;

/**
 * ClassName: UserController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/28 18:51
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        //获取前端传入的phone
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();
        //把phone做为session
        //将生成的验证码保存到session
        //session.setAttribute(phone,code);

        //使用redis存储验证码 => 验证码是前端自动生成的，这里使用redis存储意义不大
        stringRedisTemplate.opsForValue().set(phone,code);
        log.info("验证码：{}", stringRedisTemplate.opsForValue().get(phone));

        /*if (session.getAttribute("phone").equals(phone)){*/
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            //判断当前手机号是否为新用户，如果是新用户就自动完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user", user.getId());
        //用户登录成功，删除redis中的验证码缓存
        stringRedisTemplate.delete(phone);
        return R.success(user);
        //}
        /*return R.error("登录失败");*/
    }
}
