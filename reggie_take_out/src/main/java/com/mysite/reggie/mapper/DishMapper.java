package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: DishMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 12:35
 * version 1.0
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {
    //void updateStatusById(@Param("status") Integer status,@Param("ids") Long ids);

}
