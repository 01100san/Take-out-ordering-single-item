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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    private RedisTemplate redisTemplate;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);

        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page<DishDto>> page(@RequestParam("page") Integer currentPage,
                              @RequestParam Integer pageSize,
                              @RequestParam(required = false) String name){
        Page<DishDto> dishDtoPage = dishService.pageDishDto(currentPage,pageSize,name);
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
        //清理所有菜品的缓存数据
        /*Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);*/

        //清理当前分类的菜品
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

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

        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);

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
        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish*");
        redisTemplate.delete(keys);
        return R.success("删除成功");
    }

    /**
     * 管理端在添加套餐时，根据点击的categoryId回显菜品信息
     * 用户端登陆时，显示菜品
     * 使用Redis缓存菜品数据
     * @param dish
     * @return 返回dishDto类型，因为前端针对用户端 需要判断是否有dish_flavor，决定是dish或setmeal，同时请求体中的setmealId或dishId才能被接受到，
     *          否则前端接收到的dish没有 dish_flavor，请求体一直为setmealId
     */
    @GetMapping("/list")
    public R<List<DishDto>> getDishOnSetmeal(Dish dish){
        //1.设置key
        //对用户端不同种类的菜品进行分类
        String key = "dish_"+dish.getCategoryId() + "_" +dish.getStatus();
        //2.先从redis中获取缓存数据
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //3.如果存在，直接返回，无需查询数据库
        if (dishDtos != null) {
            //设置缓存的有效期不变仍为60分钟
            redisTemplate.expire(key,60,TimeUnit.MINUTES);
            return R.success(dishDtos);
        }
        //4.如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis
        dishDtos = dishService.listDishDto(dish);
        redisTemplate.opsForValue().set(key,dishDtos,60, TimeUnit.MINUTES);
        return R.success(dishDtos);
    }
}
