package com.zs.foms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zs.foms.common.R;
import com.zs.foms.entity.Orders;
import com.zs.foms.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R<Page> page(int page, int pageSize, String name) {
        return null;
    }
}