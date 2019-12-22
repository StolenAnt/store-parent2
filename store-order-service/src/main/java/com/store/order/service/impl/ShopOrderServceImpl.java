package com.store.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.store.mapper.TbOrderItemMapper;
import com.store.mapper.TbOrderMapper;
import com.store.order.service.ShopOrderService;
import com.store.pojo.TbOrder;
import com.store.pojo.TbOrderExample;
import com.store.pojo.TbOrderItem;
import com.store.pojo.TbOrderItemExample;
import com.store.pojogroup.OrderGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ShopOrderServceImpl implements ShopOrderService {

    @Autowired
    private TbOrderMapper tbOrderMapper;

    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Override
    public List<OrderGroup> findAllList(String name) {
        List<OrderGroup> orderGroupList=new ArrayList();

        TbOrderExample example=new TbOrderExample();
        TbOrderExample.Criteria criteria=example.createCriteria();
        criteria.andSellerIdEqualTo(name);
        List<TbOrder> tbOrderList = tbOrderMapper.selectByExample(example);
        for (TbOrder order:tbOrderList){
            OrderGroup orderGroup=new OrderGroup();
            orderGroup.setOrder(order);
            TbOrderItemExample example1=new TbOrderItemExample();
            TbOrderItemExample.Criteria criteria1=example1.createCriteria();
            criteria1.andOrderIdEqualTo(order.getOrderId());
            List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(example1);
            orderGroup.setOrderItems(tbOrderItems);
            orderGroupList.add(orderGroup);
        }


        return orderGroupList;
    }

    @Override
    public TbOrder findOne(Long id) {
        return null;
    }

    @Override
    public void UpdeteStatus(Long id,String status) {
        TbOrder order = tbOrderMapper.selectByPrimaryKey(id);
        System.out.println("========"+order.getReceiver()+"=========");
        System.out.println("========"+order.getReceiverAreaName()+"=========");
        System.out.println("========"+status+"=========");
        order.setStatus("3");
        tbOrderMapper.updateByPrimaryKey(order);
    }
}
