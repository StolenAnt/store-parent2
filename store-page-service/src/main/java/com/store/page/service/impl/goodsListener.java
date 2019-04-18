package com.store.page.service.impl;

import com.store.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class goodsListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String id=textMessage.getText();
            System.out.println("接收到订阅消息::::");
            boolean b = itemPageService.genItemHtml(Long.parseLong(id));
            System.out.println("网页生成结果:"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
