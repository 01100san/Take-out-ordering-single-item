package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.Setmeal;
import com.mysite.reggie.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: SetmealDishMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/27 14:53
 * version 1.0
 */
@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
