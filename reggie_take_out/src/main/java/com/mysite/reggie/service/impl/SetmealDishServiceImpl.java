package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.SetmealDish;
import com.mysite.reggie.mapper.SetmealDishMapper;
import com.mysite.reggie.service.DishService;
import com.mysite.reggie.service.SetmealDishService;
import com.mysite.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: SetmealDishServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/27 14:54
 * version 1.0
 */
@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
    @Autowired
    private DishService dishService;

    @Override
    public List<DishDto> listSetmealDishes(Long id) {
        //根据setmealId查询对应的setmeal
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = this.list(lambdaQueryWrapper);

        List<DishDto> dishes = setmealDishes.stream().map((SetmealDish item) -> {
            //用来向前端响应的dishDto对象  ==>  接收菜品的copies份数 ==> 在setmeal_dish中
            DishDto dishDto = new DishDto();
            //获取setmealDish的copies
            Integer copies = item.getCopies();
            dishDto.setCopies(copies);
            //获取setmealDish中的dishId
            Long dishId = item.getDishId();
            //根据dishId查询dish
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId, dishId);
            //获取查询到的dish数据
            Dish dish = dishService.getOne(queryWrapper);
            //将查询得到的dish信息复制给dishDto
            BeanUtils.copyProperties(dish,dishDto);
            return dishDto;
        }).collect(Collectors.toList());

        return dishes;
    }
}
