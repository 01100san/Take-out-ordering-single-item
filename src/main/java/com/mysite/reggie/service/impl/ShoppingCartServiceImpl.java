package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.entity.ShoppingCart;
import com.mysite.reggie.mapper.ShoppingCartMapper;
import com.mysite.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * ClassName: ShoppingCartServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/28 21:04
 * version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
