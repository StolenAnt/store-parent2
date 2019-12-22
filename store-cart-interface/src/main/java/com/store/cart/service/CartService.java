package com.store.cart.service;

import com.store.pojo.TbOrderItem;
import com.store.pojogroup.Cart;

import java.util.List;

public interface CartService {

    //添加商品到购物车
    public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);

    //Redis提取购物车
    public List<Cart> findCartListFormRedis(String username);

    //Redis存入购物车
    public void saveCartListToRedis(String username,List<Cart> cartList);

    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList,List<Cart> cartList2);

    public List<Cart> deleteCartList(List<Cart> cartList,Long itemId,String name);
}
