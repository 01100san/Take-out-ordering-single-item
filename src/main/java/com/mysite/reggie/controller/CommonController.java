package com.mysite.reggie.controller;

import com.mysite.reggie.common.R;
import com.mysite.reggie.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ClassName: CommonController
 * Package: com.mysite.reggie.controller
 * Description
 *  文件上传和下载
 * @Author zhl
 * @Create 2023/11/25 14:29
 * version 1.0
 */
@Slf4j
/*@RestController
@RequestMapping("/common")*/
public class CommonController {
    /*@Value("${path}")
    String basePath;*/

    /**
     * 将用户上传的文件转存到指定位置
     * @param fileUp
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile fileUp){
        //1.获取上传文件的文件名 ==> 不建议使用此方法，因为这个文件是由客户端提供的，文件名中可能有恶意字符
        /*String filename = fileUp.getOriginalFilename();*/

        //1.获取上传文件的文件名 => 获取文件类型
        String originalFilename = fileUp.getOriginalFilename();
        //2.根据源文件 获取文件类型    前端已经对上传的文件类型做了限制，因此这里就不需要判断了
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        //3.随机生成UUID，作为文件名
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + suffixName;
        //4.查找当前应用下的 file文件目录，如果不存在创建file目录
        //String filePath = request.getSession().getServletContext().getRealPath("photo");
        //realPath = C:\Users\zhl36\AppData\Local\Temp\tomcat-docbase.8080.2449399181432488676\photo
        //并不是我想要的当前的项目目录，只是存储临时文件的地方

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //4.1获取当前项目的根路径
        //resourcePath = /D:/reggie_project/reggie_take_out/target/classes/
        String resourcePath = classLoader.getResource("").getPath();
        log.info("resourcePath = " + resourcePath);
        //4.2在父目录resourcePath下，创建子目录photo目录
        File dir = new File(resourcePath,"photo");
        if (!dir.exists()){
            dir.mkdir();
        }
        //4.3将客户端的文件上传到的指定目录
        String dirPath = dir.getAbsolutePath();
        //5.将文件路径dirPath，文件名fileName拼接
        String filePath = dirPath + File.separator + fileName;
        log.info("上传后的实际路径是：{}",filePath);
        //6.将文件转存到指定位置
        try {
            fileUp.transferTo(new File(filePath));
            log.info("文件上传成功");
        } catch (IOException e) {
            log.error("文件上传失败");
            throw new FileUploadException("文件上传失败");
        }
        return R.success(fileName);
    }

    /**
     * 文件下载 最大1M
     * @param name 文件名
     * @param response
     */
    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String resourcePath = classLoader.getResource("").getPath();
        File dir = new File(resourcePath, "photo");
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //输入流，通过输入流读取文件内容
            fileInputStream = new FileInputStream(new File(dir + File.separator+ name));
            //输出流，通过输出流将文件写回浏览器，在浏览器中展示图片
            outputStream = response.getOutputStream();
            //设置响应的文件类型为 image/jpeg
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}