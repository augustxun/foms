package com.zs.foms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zs.foms.entity.DishFlavor;
import com.zs.foms.mapper.DishFlavorMapper;
import com.zs.foms.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
