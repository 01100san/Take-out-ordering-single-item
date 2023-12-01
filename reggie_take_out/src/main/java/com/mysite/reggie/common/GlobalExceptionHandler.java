package com.mysite.reggie.common;

import com.mysite.reggie.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

/**
 * ClassName: GlobalExceptionHandler
 * Package: com.mysite.reggie.common
 * Description
 *  全局异常处理器
 * @Author zhl
 * @Create 2023/11/22 20:33
 * version 1.0
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 员工添加字段username唯一异常
     */
    @ExceptionHandler(value = {SQLException.class})
    public R<String> exceptionHandler(SQLException ex){
        log.error(ex.getMessage());     //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        //判断异常的信息中是否有  Duplicate entry
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 菜品，套餐分类中绑定异常
     */
    @ExceptionHandler(value = {DishBindException.class, SetmealBindException.class})
    public R<String> customerExceptionHandler(RuntimeException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }

    /**
     * 文件上传异常
     */
    @ExceptionHandler(value = {FileUploadException.class,IOException.class})
    public R<String> fileUploadExceptionHandler(RuntimeException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }

    @ExceptionHandler(value = {SetmealStatusException.class, DishStatusException.class})
    public R<String> setmealStatusExceptionHandler(RuntimeException ex){
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }


}