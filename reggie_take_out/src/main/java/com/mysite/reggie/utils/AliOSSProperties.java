package com.mysite.reggie.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * ClassName: AliOSSProperties
 * Package: com.itheima.utils
 * Description
 *  将Utils中的配置信息封装在当前类中，注入容器中
 * @Author zhl
 * @Create 2023/7/17 10:10
 * version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss") //添加配置信息的前缀
public class AliOSSProperties {
    private String endPoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
}
