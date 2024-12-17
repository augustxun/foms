package com.zs.foms.controller;

import cn.hutool.db.sql.Order;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.foms.common.R;
import com.zs.foms.dto.DishDto;
import com.zs.foms.entity.Category;
import com.zs.foms.entity.Dish;
import com.zs.foms.entity.Employee;
import com.zs.foms.entity.Orders;
import com.zs.foms.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单
 */
@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "B端-订单管理接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    @Operation(summary = "用户下单")
    public R<String> submit(@RequestBody Orders orders) {
        log.info("订单数据：{}", orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 拉取订单详情
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    @Operation(summary = "拉取订单记录")
    public R<Page> page(int page,int pageSize,String name){

        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
//        queryWrapper.like(StringUtils.isNotEmpty(name),Orders::getName,name);
        //添加排序条件
//        queryWrapper.orderByDesc(Orders::order_time);

        //执行查询
        orderService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    @GetMapping("/chart")
    @Operation(summary = "拉取订单记录")
    public R<List<Integer>> chart(){

        // Define the start and end months (June to November)
        YearMonth startMonth = YearMonth.of(2023, 6);
        YearMonth endMonth = YearMonth.of(2023, 11);
        // List to hold the results

        ArrayList<Integer> arr = orderService.DataAnalysis(startMonth, endMonth);
        return R.success(arr);
    }

}