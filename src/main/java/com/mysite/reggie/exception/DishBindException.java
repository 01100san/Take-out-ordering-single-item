package com.mysite.reggie.exception;

/**
 * ClassName: DishBindException
 * Package: com.mysite.reggie.exception
 * Description
 *  自定义菜品异常信息
 * @Author zhl
 * @Create 2023/11/25 13:35
 * version 1.0
 */
public class DishBindException extends RuntimeException{
    private static final long serialVersionUID = 99999222241321L;
    public DishBindException(){
        super();
    }
    public DishBindException(String message){
        super(message);
    }
}
