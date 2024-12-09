package com.zs.foms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.foms.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {

}