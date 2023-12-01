package com.mysite.reggie.common;

/**
 * ClassName: BaseContext
 * Package: com.mysite.reggie.common
 * Description
 *  基于ThreadLocal的封装工具类，用来保存用户的id
 * @Author zhl
 * @Create 2023/11/23 20:27
 * version 1.0
 */
public class BaseContext {
    public static ThreadLocal<Long> local = new ThreadLocal<>();

    /**
     * 设置用户id值
     * @param id
     */
    public static void setCurrentId(Long id){
        local.set(id);
    }

    /**
     * 获取用户id值
     * @return
     */
    public static Long getCurrentId(){
        return local.get();
    }

}
