package com.mysite.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: ReggieApplication
 * Package: com.mysite.reggie
 * Description
 *
 * @Author zhl
 * @Create 2023/11/21 20:19
 * version 1.0
 */
@Slf4j
@SpringBootApplication
@EnableTransactionManagement
//开启对servlet的扫描
//@ServletComponentScan
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("项目启动成功");
    }
}
