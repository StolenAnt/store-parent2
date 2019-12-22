package com.store.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.store.pay.service.ZhiFuBaoPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ZhiFuBaoPayServiceImpl implements ZhiFuBaoPayService {

    @Value("${app_id}")
    private String appid;

    @Value("${merchant_private_key}")
    private String merchant_private_key;

    @Value("${alipay_public_key}")
    private String alipay_public_key;

    @Value("${notify_url}")
    private String notify_url;

    @Value("${return_url}")
    private String return_url;

    @Value("${sign_type}")
    private String sign_type;

    @Value("${charset}")
    private String charset;

    @Value("${gatewayUrl}")
    private String gatewayUrl;

    @Autowired
    private HttpServletRequest req;
    @Autowired
    private HttpServletResponse response;

    @Override
    public String createNative(String out_trade_no, String total_fee,String subject) {
        AlipayClient alipayClient=new DefaultAlipayClient(gatewayUrl,appid,merchant_private_key,"json",charset,alipay_public_key,sign_type);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(return_url);
        request.setNotifyUrl(notify_url);

        //添加商品信息
        Map map=new HashMap();
        map.put("out_trade_no",out_trade_no);
        map.put("total_amount",total_fee);
        map.put("subject",subject);
        map.put("body","你好,测试商品");
        map.put("product_code","FAST_INSTANT_TRADE_PAY");

        String parm=JSON.toJSONString(map);

        request.setBizContent(parm);
//        System.out.println("===这是parm:"+parm);

        String result="";
        try {
            result = alipayClient.pageExecute(request).getBody();


        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return result;
    }
}
