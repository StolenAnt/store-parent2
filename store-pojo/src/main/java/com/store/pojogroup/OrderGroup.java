package com.store.pojogroup;

import com.store.pojo.TbOrder;
import com.store.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class OrderGroup implements Serializable {

    private TbOrder order;
    private List<TbOrderItem> orderItems;


    public TbOrder getOrder() {
        return order;
    }

    public void setOrder(TbOrder order) {
        this.order = order;
    }

    public List<TbOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<TbOrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
