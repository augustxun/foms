package com.zs.foms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zs.foms.entity.Orders;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public interface OrderService extends IService<Orders> {

    /**
     * 用户下单
     * @param orders
     */
    public void submit(Orders orders);
    public ArrayList<Integer> DataAnalysis(YearMonth startMonth, YearMonth endMonth );
}
