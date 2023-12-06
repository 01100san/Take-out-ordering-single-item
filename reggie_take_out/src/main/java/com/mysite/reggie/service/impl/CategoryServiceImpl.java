package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.common.GlobalExceptionHandler;
import com.mysite.reggie.entity.Category;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.Setmeal;
import com.mysite.reggie.exception.DishBindException;
import com.mysite.reggie.exception.SetmealBindException;
import com.mysite.reggie.mapper.CategoryMapper;
import com.mysite.reggie.service.CategoryService;
import com.mysite.reggie.service.DishService;
import com.mysite.reggie.service.SetmealService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: CategoryServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/23 19:15
 * version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 删除当前分类前，判断分类是否关联了菜品或套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询关联的菜品，如果count>0,抛出异常,无法删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(id != null,Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0){
            //已经关联菜品，抛出业务异常
            throw new DishBindException("删除失败-_-|，当前的菜品分类中，包含的菜品有：" + dishCount + "种");
        }
        //查询关联了套餐，如果count>0,抛出异常,无法删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(id != null,Setmeal::getCategoryId,id);
        int setMealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setMealCount > 0){
            //已经关联套餐，抛出业务异常
            throw new SetmealBindException("删除失败-_-|，当前的套餐分类中，包含的套餐有："+setMealCount +"种");
        }
        //正在删除分类
        super.removeById(id);
    }
}
