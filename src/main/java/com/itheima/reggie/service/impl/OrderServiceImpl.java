package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomerException;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrderMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.beans.beancontext.BeanContext;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Resource
    private ShoppingCartService shoppingCartService;
    @Resource
    private UserService userService;
    @Resource
    private AddressBookService addressBookService;
    @Resource
    private OrderDetailService orderDetailService;

    @Override
    public void submitOrders(Orders orders) {
        Long userId = BaseContext.getCurrentId();
        // 查询购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);

        long orderId = IdWorker.getId();


        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomerException("购物车为空，不能下单");
        }

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCartList.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            BeanUtils.copyProperties(item, orderDetail);
            amount.addAndGet(item.getAmount().multiply((new BigDecimal(item.getNumber()))).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        // 查询用户数据
        User user = userService.getById(userId);
        
        // 查询地址数据
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (addressBook == null) {
            throw new CustomerException("用户地址信息有误，不能下单");
        }
        // 向订单表插入数据
        Orders order = Orders.builder()
                .number(String.valueOf(System.currentTimeMillis()))                 //订单号
                .status(2)                                     //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消 7退款
                .userId(userId)                                                     //用户id
                .addressBookId(addressBookId)                                       //地址id
                .orderTime(LocalDateTime.now())
                .checkoutTime(LocalDateTime.now())//下单时间                 //支付方式 1微信，2支付宝                 //支付状态 0未支付 1已支付 2退款
                .amount(new BigDecimal(amount.get()))                                //实收金额                 //备注
                .userName(user.getName())                     //用户名
                .phone(addressBook.getPhone())                                      //手机号
                .address(addressBook.getProvinceName()                              //地址
                        + addressBook.getCityName()
                        + addressBook.getDistrictName()
                        + addressBook.getDetail())
                .consignee(addressBook.getConsignee())                              //收货人//餐具数量状态 1按餐量提供  0选择具体数量
                .build();
        this.save(order);
        orderDetailService.saveBatch(orderDetails);

        // 清空购物车数据
        shoppingCartService.remove(queryWrapper);
    }
}
