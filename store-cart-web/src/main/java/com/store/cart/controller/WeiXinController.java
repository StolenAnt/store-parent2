package com.store.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.store.pay.service.WeiXinPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class WeiXinController {

    @Reference
    private WeiXinPayService weiXinPayService;

    @RequestMapping("/createNativeWeiXin")
    public Map createNativeWeiXin(){
        IdWorker idWorker=new IdWorker();

       return weiXinPayService.createNative(idWorker.nextId()+"","1");
    }
}
