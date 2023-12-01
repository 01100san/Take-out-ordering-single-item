package com.mysite.reggie.exception;

/**
 * ClassName: DishStatusException
 * Package: com.mysite.reggie.exception
 * Description
 *
 * @Author zhl
 * @Create 2023/11/29 11:13
 * version 1.0
 */
public class DishStatusException extends RuntimeException{
    private static final long serialVersionUID = 7031945766939L;
    public DishStatusException(){
        super();
    }
    public DishStatusException(String msg){
        super(msg);
    }
}
