package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetMealService extends IService<Setmeal> {
    void saveWithDishes(SetmealDto setmealDto);

    void removeWithDishes(Long ids);
}
