package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.Category;
import com.mysite.reggie.entity.Employee;
import com.mysite.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: CategoryController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/23 19:12
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
        log.info("新增的分类类别：{}",category.getType());
        log.info("新增的分类排序：{}",category.getSort());
        log.info("新增的分类名称：{}",category.getName());
        categoryService.save(category);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page<Category>> page(@RequestParam("page") Integer currentPage, @RequestParam Integer pageSize){
        Page<Category> page = new Page<>(currentPage,pageSize);
        log.info("当前页：{}",currentPage);
        log.info("最多有：{}",pageSize);
        //定义条件包装类，将Category分类按照sort的顺序排序
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(page,queryWrapper);
        return R.success(page);
    }

    @PutMapping
    public R<String> editCategory(@RequestBody Category category){
        log.info("修改的分类id：{}",category.getId());
        log.info("修改后的名称：{}",category.getName());
        log.info("修改后的排序：{}",category.getSort());
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> deleteCategory(@RequestParam Long ids){
        log.info("需要删除的菜品或套餐的id为：{}",ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /**
     * 管理端添加菜品
     * 移动端回显菜品
     * @param type  type=1菜品   type=2套餐
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(@RequestParam(required = false) Integer type){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null,Category::getType,type)
                    .orderByAsc(Category::getSort)
                    .orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
