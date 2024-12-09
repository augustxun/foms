package com.zs.foms.controller;

import com.zs.foms.service.OrderDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单明细
 */
@Slf4j
@RestController
@RequestMapping("/orderDetail")

public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

}