package com.mysite.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车
 */
@Data
public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //名称
    private String name;

    //用户id
    private Long userId;

    //菜品id
    private Long dishId;

    //套餐id
    private Long setmealId;

    //口味
    private String dishFlavor;

    //数量
    private Integer number;

    //金额
    private BigDecimal amount;

    //图片
    private String image;

    //表示不与数据库中的表相映射，但是可以被自动填充策略识别到
    //      但是经过测试，发现仍然有关于自动填充策略的其他bug /(ㄒoㄒ)/~~。
    //      所以这里就不使用自动填充策略了。数据库中默认都为null
    /*@TableField(fill = FieldFill.INSERT_UPDATE,exist = false)*/
    /*@TableField(fill = FieldFill.INSERT)*/
    private LocalDateTime createTime;


}
