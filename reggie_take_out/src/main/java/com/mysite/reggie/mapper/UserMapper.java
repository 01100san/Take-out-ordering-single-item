package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/28 18:58
 * version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
