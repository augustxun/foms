package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetMealMapper;
import com.itheima.reggie.service.SetMealService;
import com.itheima.reggie.service.SetmealDishService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
class SetMealServiceImpl extends ServiceImpl<SetMealMapper, Setmeal> implements SetMealService {
    @Resource
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDishes(SetmealDto setmealDto) {
        // 同时对 setmeal 和 setmeal_dish 两张表进行处理
        this.save(setmealDto);
        Long setmealDtoId = setmealDto.getId();
        List<SetmealDish> dishList = setmealDto.getSetmealDishes().stream().map((item) -> {
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishList);
    }

    @Override
    @Transactional
    public void removeWithDishes(Long ids) {
        removeById(ids);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper);
    }
}
