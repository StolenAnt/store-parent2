package com.store.order.service;

import com.store.pojo.TbOrder;
import com.store.pojogroup.OrderGroup;

import java.util.List;

public interface ShopOrderService {

    public List<OrderGroup> findAllList(String name);

    public TbOrder findOne(Long id);

    public void UpdeteStatus(Long id,String status);
}
