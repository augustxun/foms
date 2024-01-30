package com.itheima.reggie.controller.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Resource
    private SetMealService setMealService;

    @Resource
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        // 分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        // 条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 添加过滤条件
        queryWrapper.like(name != null, Setmeal::getName, name);
        // 添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 执行分页查询
        setMealService.page(pageInfo, queryWrapper);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        // 处理 records 属性
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list =  records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 普通属性拷贝到 dishDto
            BeanUtils.copyProperties(item, setmealDto);
            String categoryName = categoryService.getById(item.getCategoryId()).getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);

        // 返回分页构造器对象
        return R.success(setmealDtoPage);
    }

    @GetMapping("/list")


    @PostMapping()
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setMealService.saveWithDishes(setmealDto);
        return R.success("添加套餐成功");
    }

    @DeleteMapping
    public R<String> remove(Long ids) {
        setMealService.removeWithDishes(ids);
        return R.success("删除成功");
    }

}
