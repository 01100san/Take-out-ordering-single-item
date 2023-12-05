package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import org.apache.commons.lang.StringUtils;
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
    private CategoryService categoryService;

    @Override
    public Page<DishDto> pageDishDto(Integer currentPage, Integer pageSize, String name) {
        Page<Dish> page = new Page<>(currentPage,pageSize);
        //在Dish中只有category_id分类id ，没有categoryName
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);
        //这里的分页需要将 categoryName 显示在 浏览器上，但是dish表中只有categoryId 字段
        //想要获取categoryName 必须查询 category表
        this.page(page,queryWrapper);

        BeanUtils.copyProperties(page,dishDtoPage,"records");
        List<Dish> records = page.getRecords();

        List<DishDto> list = records.stream().map((Dish item) -> {
            DishDto dishDto = new DishDto();
            //将dish属性赋值给dishDto
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据categoryId查询分类对象category
            Category category = categoryService.getById(categoryId);
            if(category != null){
                //String categoryName = category.getName();
                //将categoryName属性赋值给dishDto
                dishDto.setCategoryName(category.getName());
            }
            return dishDto;
            //循环遍历最后以list集合的形式返回
        }).collect(Collectors.toList());
        //将List<DishDto> 赋值给dishDtoPage<DishDto>中的records集合
        dishDtoPage.setRecords(list);
        return dishDtoPage;
    }

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
    }

    @Override
    public void updateByIdWithStatus(Integer status,List<Long> ids) {
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

    /**
     * 在添加套餐时，根据点击的categoryId回显菜品信息
     * 用户端登陆时，显示菜品
     * @param dish
     * @return 返回dishDto类型，因为前端针对用户端 需要判断是否有dish_flavor，决定是dish或setmeal，同时请求体中的setmealId或dishId才能被接受到，
     *          否则前端接收到的dish没有 dish_flavor，请求体一直为setmealId
     */
    @Override
    public List<DishDto> listDishDto(Dish dish) {
        Long categoryId = dish.getCategoryId();
        Integer status = dish.getStatus();
        String dishName = dish.getName();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId)
                .eq(status != null,Dish::getStatus,1)
                .like(StringUtils.isNotEmpty(dishName),Dish::getName,dishName)  //管理端：添加套餐时 添加菜品直接根据菜品名字 查询
                .orderByAsc(Dish::getUpdateTime);

        //根据条件（categoryId,status,dishName）=> required=false查询
        List<Dish> dishes = this.list(queryWrapper);
        List<DishDto> dishDtos = dishes.stream().map((Dish item) -> {
            DishDto dishDto = new DishDto();
            //根据categoryId为dishDto中的categoryName属性赋值
            Category category = categoryService.getById(categoryId);
            if (category != null){
                dishDto.setCategoryName(category.getName());
            }
            //获取dishId
            Long id = item.getId();
            //根据dish_id查询对应的菜品口味
            List<DishFlavor> flavors = this.getByIdWithFlavors(id);
            //将dish信息复制给dishDto对象
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return dishDtos;
    }
}