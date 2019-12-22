package com.store.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.store.order.service.OrderService;
import com.store.pojo.TbOrder;

import com.store.pojogroup.OrderGroup;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/findOrderListByUser")
    public List<OrderGroup> findOrderListByUser(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return orderService.findOrderListByUser(name);
    }
}
