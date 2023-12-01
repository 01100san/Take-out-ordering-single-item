package com.mysite.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mysite.reggie.entity.Category;

/**
 * ClassName: CategoryService
 * Package: com.mysite.reggie.service
 * Description
 *
 * @Author zhl
 * @Create 2023/11/23 19:13
 * version 1.0
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
