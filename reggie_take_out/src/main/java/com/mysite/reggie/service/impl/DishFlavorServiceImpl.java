package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.entity.DishFlavor;
import com.mysite.reggie.mapper.DishFlavorMapper;
import com.mysite.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

/**
 * ClassName: DishFlavorServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/26 14:09
 * version 1.0
 */
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
