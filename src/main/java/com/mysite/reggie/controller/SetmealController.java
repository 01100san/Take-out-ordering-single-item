package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysite.reggie.common.R;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.dto.SetmealDto;
import com.mysite.reggie.entity.Category;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.Setmeal;
import com.mysite.reggie.entity.SetmealDish;
import com.mysite.reggie.service.CategoryService;
import com.mysite.reggie.service.DishService;
import com.mysite.reggie.service.SetmealDishService;
import com.mysite.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: SetmealController
 * Package: com.mysite.reggie.controller
 * Description
 *  套餐管理
 * @Author zhl
 * @Create 2023/11/25 12:38
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    /**
     * 新增套餐
     * 更新setmeal_dish表 => setmeal_id
     * 更新setmeal表
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam("page") Integer currentPage,
                                 @RequestParam Integer pageSize,
                                 @RequestParam(required = false) String name){

        Page<Setmeal> setmealPage = new Page<>(currentPage,pageSize);
        //先声明一个Page<SetmealDto>的变量，可以不赋currentPage和pageSize值，之后会把Page<Setmeal>的属性值赋给setmealDtoPage
        Page<SetmealDto> setmealDtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name)
                    .orderByAsc(Setmeal::getUpdateTime);
        //分页查询查找到了setmeal对应的值setmeals。
        setmealService.page(setmealPage,queryWrapper);

        //不能直接将setmeals的记录值直接赋给 records，我先将currentPage和pageSize值赋给re
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> setmeals = setmealPage.getRecords();

        //根据categoryId设置categoryName
        List<SetmealDto> records = setmeals.stream().map((Setmeal setmeal) -> {
            SetmealDto setmealDto = new SetmealDto();
            //把setmeal的属性赋给setmealDto
            BeanUtils.copyProperties(setmeal,setmealDto);

            Long categoryId = setmealDto.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                setmealDto.setCategoryName(category.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //为setmealDtoPage中的records赋值
        setmealDtoPage.setRecords(records);

        //前端页面想要展示categoryName套餐分类的值，但是在setmeal中只有categoryId值。
        //通过为setmealDto中的categoryName赋值即可得到
        //最后返回给前端的数据是setmealDtoPage,即setmealDto对象,所以要给setmealDtoPage的categoryName赋值，为records集合赋值
        return R.success(setmealDtoPage);
    }

    /**
     * 根据id查询对应的套餐信息和菜品信息 用于回显
     * uri中的 ? 是SpringMVC中的ant风格，表示任意的单个字符
     * uri中的 * 是SpringMVC中的ant风格，表示任意的0个或多个字符
     * uri中的 ** 是SpringMVC中的ant风格，表示任意的一层或多层目录
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDishes(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> editSetmealDto(@RequestBody SetmealDto setmealDto){
        setmealService.updateByIdWithDishes(setmealDto);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        //setmealDishService.removeByIds(ids);
        setmealService.removeByIdWithDishes(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> statusSetmeal(@PathVariable Integer status,@RequestParam("ids") List<Long> ids){
        setmealService.updateByIdWithStatus(status,ids);
        return R.success("状态修改成功");
    }

    /**
     * 获取用户端对应的套餐
     * @param categoryId 分类id
     * @param status 套餐状态
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam Long categoryId, @RequestParam Integer status){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(status != null,Setmeal::getStatus,1)
                        .eq(Setmeal::getCategoryId,categoryId);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);
    }

    /**
     * 用户端在点击套餐的图片时，显示但钱套餐下的全部菜品
     * 根据setmealId查询 setmeal_dish
     * @param id setmealId
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable Long id){
        /*LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.list(queryWrapper);
        */
        //根据setmealId查询对应的setmeal
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(lambdaQueryWrapper);

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

        return R.success(dishes);
    }
}
