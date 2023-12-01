package com.mysite.reggie.exception;

/**
 * ClassName: SetmealBindException
 * Package: com.mysite.reggie.exception
 * Description
 *  自定义套餐异常信息
 * @Author zhl
 * @Create 2023/11/25 13:35
 * version 1.0
 */
public class SetmealBindException extends RuntimeException{
    private static final long serialVersionUID = 2123123123131666L;
    public SetmealBindException(){
        super();
    }
    public SetmealBindException(String message){
        super(message);
    }
}
