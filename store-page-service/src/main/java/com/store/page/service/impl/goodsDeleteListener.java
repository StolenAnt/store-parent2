package com.store.page.service.impl;


import com.store.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class goodsDeleteListener implements MessageListener {


    @Autowired
    private ItemPageService itemPageService;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage= (ObjectMessage) message;
        try {
            Long[] ids= (Long[]) objectMessage.getObject();
            boolean b = itemPageService.deleteItemHtml(ids);
            System.out.println("接受删除消息.......网页删除:"+b);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
