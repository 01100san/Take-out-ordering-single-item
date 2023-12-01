package com.mysite.reggie.exception;

/**
 * ClassName: SetmealStatusException
 * Package: com.mysite.reggie.exception
 * Description
 *  套餐状态异常
 * @Author zhl
 * @Create 2023/11/28 13:49
 * version 1.0
 */
public class SetmealStatusException extends RuntimeException{
    private static final long serialVersionUID = 70348971945766939L;
    public SetmealStatusException(){
        super();
    }
    public SetmealStatusException(String msg){
        super(msg);
    }
}
