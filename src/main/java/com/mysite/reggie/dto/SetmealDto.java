package com.mysite.reggie.dto;

import com.mysite.reggie.entity.Setmeal;
import com.mysite.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
