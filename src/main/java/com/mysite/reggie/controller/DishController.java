package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysite.reggie.common.R;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.entity.Category;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.DishFlavor;
import com.mysite.reggie.service.CategoryService;
import com.mysite.reggie.service.DishFlavorService;
import com.mysite.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ClassName: DisController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 13:08
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(@RequestParam("page") Integer currentPage,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String name){

        Page<Dish> page = new Page<>(currentPage,pageSize);
        //在Dish中只有category_id分类id ，没有categoryName
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name)
                    .orderByAsc(Dish::getSort)
                    .orderByDesc(Dish::getUpdateTime);
        //这里的分页需要将 categoryName 显示在 浏览器上，但是dish表中只有categoryId 字段
        //想要获取categoryName 必须查询 category表
        dishService.page(page,queryWrapper);

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
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDishDto(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> editDishDto(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功");
    }

    /**
     * 批量修改菜品状态
     * @param status 0停售 1起售
     * @param ids 多个菜品id
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> statusDish(@PathVariable Integer status,@RequestParam("ids") List<Long> ids){
        dishService.updateByIdWithStatus(status,ids);
        return R.success("修改状态成功");
    }

    /**
     * 批量删除多个菜品和当中的flavor
     * 菜品可以逻辑删除，口味直接删除，不保存
     * @param ids 多个菜品id
     * @return
     */
    @DeleteMapping
    public R<String> deleteDish(@RequestParam("ids") List<Long> ids){
        /*dishService.removeByIds(ids);*/
        dishService.removeByIdsWithFlavor(ids);
        return R.success("删除成功");
    }

    /**
     * 在添加套餐时，根据点击的categoryId回显菜品信息
     * 用户端登陆时，显示菜品
     * @param dish
     * @return 返回dishDto类型，因为前端针对用户端 需要判断是否有dish_flavor，决定是dish或setmeal，同时请求体中的setmealId或dishId才能被接受到，
     *          否则前端接收到的dish没有 dish_flavor，请求体一直为setmealId
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishOnSetmeal(Dish dish/*,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) Long categoryId*/){
        Long categoryId = dish.getCategoryId();
        Integer status = dish.getStatus();
        String dishName = dish.getName();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null,Dish::getCategoryId,categoryId)
                    .eq(status != null,Dish::getStatus,1)
                    .like(StringUtils.isNotEmpty(dishName),Dish::getName,dishName)  //添加套餐时 添加菜品直接根据菜品名字 查询
                    .orderByAsc(Dish::getUpdateTime);

        //根据条件（categoryId,status,dishName）=> required=false查询
        List<Dish> dishes = dishService.list(queryWrapper);
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
            List<DishFlavor> flavors = dishService.getByIdWithFlavors(id);
            //将dish信息复制给dishDto对象
            BeanUtils.copyProperties(item,dishDto);
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtos);
    }
}
