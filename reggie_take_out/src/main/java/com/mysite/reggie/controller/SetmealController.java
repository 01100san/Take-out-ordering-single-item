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
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        //清理所有套餐的缓存数据
        Set keys = redisTemplate.keys("setmeal*");
        redisTemplate.delete(keys);
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
    public R<String> editSetmealDto(@RequestBody SetmealDto setmealDto){
        setmealService.updateByIdWithDishes(setmealDto);
        //清理所有套餐的缓存数据
        Set keys = redisTemplate.keys("setmeal*");
        redisTemplate.delete(keys);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> ids){
        setmealService.removeByIdWithDishes(ids);
        //清理所有套餐的缓存数据
        Set keys = redisTemplate.keys("setmeal*");
        redisTemplate.delete(keys);
        return R.success("删除成功");
    }

    @PostMapping("/status/{status}")
    public R<String> statusSetmeal(@PathVariable Integer status,@RequestParam("ids") List<Long> ids){
        setmealService.updateByIdWithStatus(status,ids);
        //清理所有套餐的缓存数据
        Set keys = redisTemplate.keys("setmeal*");
        redisTemplate.delete(keys);
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
    public R<List<Setmeal>> list(@RequestParam Long categoryId, @RequestParam Integer status){
        //1.定义套餐key
        String key = "setmeal_" + categoryId + "_" + status;
        //2.先从Redis中查询是否存在
        List<Setmeal> setmeals = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        //3.如果存在，直接返回
        if (setmeals != null){
            //设置缓存的有效期不变仍为60分钟
            redisTemplate.expire(key,60,TimeUnit.MINUTES);
            return R.success(setmeals);
        }
        //4.如果不存在，查询后，保存到Redis
        setmeals = setmealService.listSetmeals(categoryId,status);
        redisTemplate.opsForValue().set(key,setmeals,60, TimeUnit.MINUTES);
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
