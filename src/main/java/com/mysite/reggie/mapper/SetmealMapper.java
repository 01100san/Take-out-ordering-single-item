package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ClassName: SetmealMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 12:34
 * version 1.0
 */
@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    //void updateStatusById(@Param("status") Integer status, @Param("id") Long id);
}
