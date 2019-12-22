package cn.itheimayi.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {

    @Autowired
    private SmsUtil smsUtil;

    @JmsListener(destination = "sms")
    public void SendSms(Map map) throws ClientException {

        String mobile= (String) map.get("mobile");
        String templateCode= (String) map.get("template_code");
        String sign_name= (String) map.get("sign_name");
        String param= (String) map.get("param");
        SendSmsResponse response = smsUtil.sendSms(mobile, templateCode, sign_name, param);
        System.out.println("Code:"+response.getCode());
        System.out.println("message"+response.getMessage());
    }
}
