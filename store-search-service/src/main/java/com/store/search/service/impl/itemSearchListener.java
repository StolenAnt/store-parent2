package com.store.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.store.pojo.TbItem;
import com.store.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class itemSearchListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        TextMessage textMessage= (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println("监听到消息"+text);

            List<TbItem> items = JSON.parseArray(text, TbItem.class);

            //异步消息导入列表
            itemSearchService.importList(items);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
