package com.store.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.store.order.service.OrderService;
import com.store.order.service.ShopOrderService;
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
    private ShopOrderService shopOrderService;

    @RequestMapping("findAll")
    public List<OrderGroup> findAll(){
        String name=SecurityContextHolder.getContext().getAuthentication().getName();
        return shopOrderService.findAllList(name);
    }

    @RequestMapping("updeteStatus")
    public void  updeteStatus(Long id,String status){
        shopOrderService.UpdeteStatus(id,status);
    }
}
