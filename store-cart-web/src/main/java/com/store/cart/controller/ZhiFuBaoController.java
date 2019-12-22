package com.store.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.store.order.service.OrderService;
import com.store.pay.service.ZhiFuBaoPayService;
import com.store.pojo.TbPayLog;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class ZhiFuBaoController {

    @Reference
    private ZhiFuBaoPayService zhiFuBaoPayService;

    @Reference
    private OrderService orderService;


    @RequestMapping("/ZhiFuBao")
    public void createNativeZhiFuBao(HttpServletResponse response) throws IOException {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(name);
        String result="";
        if (tbPayLog!=null){
            result = zhiFuBaoPayService.createNative(tbPayLog.getOutTradeNo(),tbPayLog.getTotalFee()+"","黑蚂蚁商城家的商品");
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(result);//直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
        }else{
            return;
        }
    }


    @RequestMapping("/success")
    public void success(HttpServletRequest request, HttpServletResponse response){
        Map<String,String> parem=new HashMap();
        Map map=request.getParameterMap();
        for (Iterator iterator=map.keySet().iterator();iterator.hasNext();){
            String name= (String) iterator.next();
            String[] values= (String[]) map.get(name);
            String vlaue="";
            for (int i = 0; i < values.length; i++) {
                vlaue = (i == values.length - 1) ? vlaue + values[i]
                        : vlaue + values[i] + ",";
            }
            parem.put(name,vlaue);
        }

        String outId=parem.get("out_trade_no");
        String trade_no=parem.get("trade_no");
        orderService.updateOrderStatus(outId,trade_no);
        System.out.println("支付宝流水号:"+trade_no);
        try {
            response.sendRedirect("../paysuccess.html");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
