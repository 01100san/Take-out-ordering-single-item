package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.User;
import com.mysite.reggie.service.UserService;
import com.mysite.reggie.utils.SMSUtils;
import com.mysite.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.awt.print.PrinterJob;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    //使用阿里云服务向用户发送验证码
    @Autowired
    private SMSUtils smsUtils;
    @Value("${aliyun.sms.signName}")
    private String signName;
    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //随机4位生成验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("验证码code：{}",code);

            //使用阿里云向用户发送验证码
            smsUtils.sendMessage(signName, templateCode, phone,code);

            //将验证码存储在session
            //session.setAttribute(phone,code);

            //将验证码存储在Redis中，有效时间5分钟
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码发送成功");
        }

        return R.error("手机验证码发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());
        //获取用户输入的phone和code
        String phone = map.get("phone").toString();
        String userCode = map.get("code").toString();

        //从Redis中根据phone获取手机验证码
        String code = stringRedisTemplate.opsForValue().get(phone);

        //进行验证码的比对
        if (code != null && code.equals(userCode)){
            User user = userService.checkOrSave(phone);
            //将用户的id存入session
            session.setAttribute("user", user.getId());
            //用户登录成功，删除redis中的验证码缓存
            stringRedisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登录失败");
    }
}
