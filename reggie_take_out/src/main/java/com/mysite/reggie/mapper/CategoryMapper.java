package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: CategoryMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/23 19:15
 * version 1.0
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
