package com.zs.foms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.foms.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}