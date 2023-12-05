package com.mysite.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysite.reggie.dto.SetmealDto;
import com.mysite.reggie.entity.Setmeal;

import java.util.List;

/**
 * ClassName: SetmealService
 * Package: com.mysite.reggie.service
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 12:35
 * version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    SetmealDto getByIdWithDishes(Long id);

    void updateByIdWithDishes(SetmealDto setmealDto);

    void removeByIdWithDishes(List<Long> ids);

    void updateByIdWithStatus(Integer status, List<Long> ids);

    Page<SetmealDto> pageSetmealDto(Integer currentPage, Integer pageSize, String name);

    List<Setmeal> listSetmeals(Long categoryId, Integer status);
}
