package com.store.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.store.pay.service.WeiXinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinServiceImpl implements WeiXinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        //1.参数封装
        Map param=new HashMap();
        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("nonce_str",WXPayUtil.generateNonceStr());
        param.put("body","黑蚂蚁");
        param.put("out_trade_no",out_trade_no);
        param.put("total_fee",total_fee);
        param.put("spbill_create_ip","127.0.0.1");
        param.put("notify_url","http://www.itheimayi.cn");
        param.put("trade_type","NATIVE");
        try {
            String paramXml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求参数"+paramXml);

            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            //3.获取结果
            String content = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(content);
            System.out.println("返回结果"+mapResult);
            Map map=new HashMap();
            map.put("code_url",mapResult.get("code_url"));
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }

    }
}
