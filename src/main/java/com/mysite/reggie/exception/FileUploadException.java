package com.mysite.reggie.exception;

import java.io.IOException;

/**
 * ClassName: FileUploadException
 * Package: com.mysite.reggie.exception
 * Description
 *  文件上传异常
 * @Author zhl
 * @Create 2023/11/25 14:44
 * version 1.0
 */
public class FileUploadException extends RuntimeException {
    private static final long serialVersionUID = 7818375828146090L;
    public FileUploadException(){
        super();
    }
    public FileUploadException(String message){
        super(message);
    }
}
