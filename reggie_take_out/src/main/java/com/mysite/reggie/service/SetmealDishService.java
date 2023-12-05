package com.mysite.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.entity.SetmealDish;

import java.util.List;

/**
 * ClassName: SetmealDishService
 * Package: com.mysite.reggie.service
 * Description
 *
 * @Author zhl
 * @Create 2023/11/27 14:54
 * version 1.0
 */
public interface SetmealDishService extends IService<SetmealDish> {
    List<DishDto> listSetmealDishes(Long id);
}
