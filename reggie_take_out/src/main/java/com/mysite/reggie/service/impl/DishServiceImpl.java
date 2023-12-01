package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.common.R;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.entity.Category;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.DishFlavor;
import com.mysite.reggie.exception.DishStatusException;
import com.mysite.reggie.mapper.DishMapper;
import com.mysite.reggie.service.CategoryService;
import com.mysite.reggie.service.DishFlavorService;
import com.mysite.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: DishServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 12:37
 * version 1.0
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增菜品，同时插入菜品对应的口味数据，需要更新两张表：dish，dish_flavor
     * @param dishDto DishDto中封装了有关 dish_flavor的信息
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //获取菜品id =》 将菜品id 放在 dish_flavor表中
        Long dishId = dishDto.getId();
        //获取菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*flavors = flavors.stream().map((DishFlavor item) -> {
            //遍历添加的口味flavor，保存菜品id到菜品口味表dish_flavor
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());*/

        for (DishFlavor flavor: flavors){
            flavor.setDishId(dishId);
        }

        //将口味数据添加到dish_flavor表中
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品的基本信息 和对应的 口味信息
     * @param id dishId 菜品id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //把 dish和dishDto中相同的属性值，赋给dishDto
        BeanUtils.copyProperties(dish,dishDto);
        //根据菜品id查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //为dishDto 填充flavors的值
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 修改菜品
     * 更新 dish表中的信息，更新 dish_flavor表中的信息
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表中的信息
        this.updateById(dishDto);
        //先清除dishId对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        Long dishId = dishDto.getId();
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors){
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);

        /*//获取dishId =》 按照dishId 更新dish_flavor中的信息
        Long dishId = dishDto.getId();
        //更新dish_flavor表中的字段
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors){
            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            //按照dishId更新dish_flavor中的数据
            queryWrapper.eq(DishFlavor::getDishId,dishId);
            dishFlavorService.update(flavor,queryWrapper);
        }*/
    }

    @Override
    public void updateByIdWithStatus(Integer status,List<Long> ids) {
        /*for (Long id : ids){
            dishMapper.updateStatusById(status,id);
        }
        */
        UpdateWrapper<Dish> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status",status).in("id",ids);
        this.update(updateWrapper);
    }

    /**
     * 判断菜品的状态，正在售卖，抛出异常,停售，正常删除
     * 先根据dish_id删除dish_flavor中的flavor
     * 再根据ids删除菜品
     * @param ids dish_id的集合
     */
    @Transactional
    @Override
    public void removeByIdsWithFlavor(List<Long> ids) {
        //判断菜品的状态 1=起售 0=停售
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Dish::getId,ids)
                    .eq(Dish::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0){
            throw new DishStatusException("菜品正在售卖中，不能删除");
        }
        //按照dish_id删除dish_flavor中的flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper);
        //再根据ids删除菜品
        this.removeByIds(ids);
    }

    /**
     * 按照dishId查询口味
     * @param id
     * @return
     */
    @Override
    public List<DishFlavor> getByIdWithFlavors(Long id) {
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        return dishFlavorService.list(queryWrapper);
    }
}