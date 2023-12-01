package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.ShoppingCart;
import com.mysite.reggie.service.CategoryService;
import com.mysite.reggie.service.SetmealService;
import com.mysite.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.xml.ws.handler.LogicalHandler;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ShoppingCartController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/28 21:00
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 展示购物车中的商品列表
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        List<ShoppingCart> list = shoppingCartService.list();
        return R.success(list);
    }

    /**
     * 用户端向购物车中添加订单
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
        Long userId = (Long) session.getAttribute("user");
        log.info("user_id：{}",userId);

        shoppingCart.setUserId(userId);
        shoppingCartService.save(shoppingCart);
        return R.success(shoppingCart);
    }

    /**
     * 删除购物车中的订单
     * 不能根据dishId删除，如果同种菜品或套餐添加了多个，那么用户想要删除一个套餐或菜品，会导致直接把这种setmealId或dishId清空
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<String> subGood(@RequestBody ShoppingCart shoppingCart
                            /*@RequestParam(required = false) Long dishId,
                             @RequestParam(required = false) Long setmealId*/){
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        log.info("dishId: {}",dishId);
        log.info("setmealId: {}",setmealId);

        //在shopping_cart表中 根据dishId和id查询 对应的物品
        //先根据 dish_id 或 setmeal_id 查询 对应的shopping_cart表中的物品集合
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId)
                        .or()
                        .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId);
        List<ShoppingCart> list = shoppingCartService.list(lambdaQueryWrapper);
        //再从查询的物品集合中循环删除一个元素
        for (ShoppingCart shoppingCart1 : list){
            Long id = shoppingCart1.getId();
            log.info("购物车中要删除的的 id：{}",id);
            /*//Long dishId1 = shoppingCart1.getDishId();
            LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
            //delete from shopping_cart where dish_id = #{dishId} or setmeal_id = #{setmealId}
            queryWrapper.eq(ShoppingCart::getId,id)
                    .and(i -> i.eq(dishId != null,ShoppingCart::getDishId,dishId).
                            or().eq(setmealId != null,ShoppingCart::getSetmealId,setmealId));
            shoppingCartService.remove(queryWrapper);
            */
            shoppingCartService.removeById(id);
            return setmealId == null
                    ? R.success("成功删除: " + dishId + "号菜品")
                    : R.success("成功删除: " + setmealId + "号套餐");
        }
        return R.error("删除失败😟");
        /*LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //delete from shopping_cart where dish_id = #{dishId} or setmeal_id = #{setmealId}
        queryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId)
                    .or()
                    .eq(setmealId != null,ShoppingCart::getSetmealId,setmealId);
        shoppingCartService.remove(queryWrapper);
        return setmealId == null
                ? R.success("成功删除: " + dishId + "号菜品")
                : R.success("成功删除: " + setmealId + "号套餐");
        */
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> cleanGood(){
        boolean flag = shoppingCartService.remove(null);
        return flag ? R.success("清除成功😊") : R.error("购物车列表为空😰");
    }


}
