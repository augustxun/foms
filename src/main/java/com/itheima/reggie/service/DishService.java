package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表 dish 和 dish_flavor

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);


    /**
     * 根据 id 查询菜品和对应的口味信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);


    void updateWithFlavor(DishDto dishDto);
}
