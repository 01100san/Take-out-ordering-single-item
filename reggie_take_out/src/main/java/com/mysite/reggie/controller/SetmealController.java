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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * 更新setmeal_dish表 => setmeal_id
     * 更新setmeal表
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam("page") Integer currentPage,
                                 @RequestParam Integer pageSize,
                                 @RequestParam(required = false) String name){
        Page<SetmealDto> setmealDtoPage = setmealService.pageSetmealDto(currentPage,pageSize,name);
        return R.success(setmealDtoPage);
    }

    /**
     * 根据id查询对应的套餐信息和菜品信息 用于回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getSetmeal(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDishes(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> editSetmealDto(@RequestBody SetmealDto setmealDto){
        setmealService.updateByIdWithDishes(setmealDto);
        return R.success("修改成功");
    }
    @DeleteMapping
    //删除当前value中的所有缓存
    @CacheEvict(value = "setmealCache", allEntries = true)
    //这里的ids 是setmealId不是存入Redis中的categoryId，所以不能根据ids删除缓存，建议删除所有
    //@CacheEvict(value = "setmealCache", key = "#ids")
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        setmealService.removeByIdWithDishes(ids);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> statusSetmeal(@PathVariable Integer status,@RequestParam("ids") List<Long> ids){
        setmealService.updateByIdWithStatus(status,ids);
        return R.success("状态修改成功");
    }

    /**
     * 获取用户端对应的套餐
     * 使用Redis缓存套餐数据
     * @param categoryId 分类id
     * @param status 套餐状态
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#categoryId + '_' + #status", unless = "#result == null")
    public R<List<Setmeal>> list(@RequestParam Long categoryId, @RequestParam Integer status){
        List<Setmeal> setmeals = setmealService.listSetmeals(categoryId,status);
        return R.success(setmeals);
    }

    /**
     * 用户端在点击套餐的图片时，显示当前套餐下的全部菜品
     * 根据setmealId查询 setmeal_dish
     * @param id setmealId
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<DishDto>> dish(@PathVariable Long id){
        List<DishDto> dishes = setmealDishService.listSetmealDishes(id);
        return R.success(dishes);
    }
}
