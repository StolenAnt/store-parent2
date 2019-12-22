package com.store.pay.service;

import java.util.Map;

public interface WeiXinPayService {



    public Map createNative(String out_trade_no,String total_fee);

}
