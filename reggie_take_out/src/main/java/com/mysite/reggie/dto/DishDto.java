
package com.mysite.reggie.dto;

import com.mysite.reggie.entity.Dish;
import com.mysite.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入DishDto，用于封装页面提交的数据
 * DTO ，全称 Data Transfer Object ，即数据传输对象
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
