package com.itheima.reggie.controller.impl;


import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.utils.RedisConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {
    @Resource
    private DishService dishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private DishFlavorService dishFlavorService;


    @Resource
    StringRedisTemplate stringRedisTemplate;
    /**
     * 将菜品管理页面分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // 分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Dish::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        // 执行分页查询
        dishService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        // 处理 records 属性
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list =  records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            // 普通属性拷贝到 dishDto
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); // 分类 id
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        // 返回分页构造器对象
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        return R.success(dishService.getByIdWithFlavor(id));
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Long categoryId, Integer status) {
        // 从 Redis 中获取缓存数据
        String dishListKey = RedisConstants.CACHE_DISH + categoryId + status;
        Long listSize = stringRedisTemplate.opsForList().size(dishListKey);
        List<String> dishDtoJsonList = stringRedisTemplate.opsForList().range(dishListKey, 0, listSize);
       if (dishDtoJsonList != null && listSize != 0) { // 从缓存中可以读出数据
           List<DishDto> dishDtoList = new ArrayList<>();
           for (String dishJson : dishDtoJsonList) {
               dishDtoList.add(JSONUtil.toBean(dishJson, DishDto.class));
           }
           return R.success(dishDtoList);
       }
        // 缓存中没有数据, 到数据库中查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId).eq(Dish::getStatus, 1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            LambdaQueryWrapper<DishFlavor> queryWrapper2 = new LambdaQueryWrapper<>();
            queryWrapper2.eq(DishFlavor::getDishId, item.getId());
            dishDto.setFlavors(dishFlavorService.list(queryWrapper2));
            return dishDto;
        }).collect((Collectors.toList()));
//        把该数据添加到缓存中去
        dishDtoJsonList = new ArrayList<>();
        for (DishDto dishDto : dishDtoList) {
            dishDtoJsonList.add(JSONUtil.toJsonStr(dishDto));
        }
        stringRedisTemplate.opsForList().rightPushAll(dishListKey, dishDtoJsonList);
        return R.success(dishDtoList);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return R.success("菜品修改成功!");
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }




}
