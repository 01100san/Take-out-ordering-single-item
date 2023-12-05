package com.mysite.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mysite.reggie.dto.DishDto;
import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.DishFlavor;

import java.util.List;

/**
 * ClassName: DishService
 * Package: com.mysite.reggie.service
 * Description
 *
 * @Author zhl
 * @Create 2023/11/25 12:36
 * version 1.0
 */
public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);
    DishDto getByIdWithFlavor(Long id);
    void updateWithFlavor(DishDto dishDto);
    void updateByIdWithStatus(Integer status, List<Long> ids);
    void removeByIdsWithFlavor(List<Long> ids);

    List<DishFlavor> getByIdWithFlavors(Long id);

    List<DishDto> listDishDto(Dish dish);

    Page<DishDto> pageDishDto(Integer currentPage, Integer pageSize, String name);
}
