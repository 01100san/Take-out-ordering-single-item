package com.mysite.reggie.controller;

import com.mysite.reggie.common.R;
import com.mysite.reggie.exception.FileUploadException;
import com.mysite.reggie.utils.AliOSSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * ClassName: AliOSSController
 * Package: com.mysite.reggie.controller
 * Description
 *  使用阿里云存储OSS上传数据
 * @Author zhl
 * @Create 2023/11/25 22:16
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class AliOSSController {
    @Autowired
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file")MultipartFile file){
        log.info("上传的文件名为：{}",file.getOriginalFilename());
        String url;
        try {
            url = aliOSSUtils.upload(file);
        } catch (IOException e) {
            throw new FileUploadException("文件上传失败");
        }
        log.info("文件上传成功的路径地址为：{}",url);
        return R.success(url);
    }
}
