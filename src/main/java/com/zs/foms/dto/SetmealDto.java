package com.zs.foms.dto;

import com.zs.foms.entity.Setmeal;
import com.zs.foms.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
