package com.buer.regi.dto;


import com.buer.regi.entity.Setmeal;
import com.buer.regi.entity.SetmealDish;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;

}
