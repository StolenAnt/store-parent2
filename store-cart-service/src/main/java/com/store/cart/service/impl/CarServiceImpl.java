package com.store.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.store.cart.service.CartService;
import com.store.mapper.TbItemCatMapper;
import com.store.mapper.TbItemMapper;
import com.store.pojo.TbItem;
import com.store.pojo.TbItemCat;
import com.store.pojo.TbOrderItem;
import com.store.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.CookieUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据SKU ID查询商品明细对象
        TbItem item=itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在");
        }
        if(!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        //2.根据SKU对象得到商家Id
        String sellerId=item.getSellerId();

        //3.根据商家Id查找在购物车列表中查询对象
        Cart cart = searchCartBySellerId(cartList, sellerId);
        //4.购物车列表中不存在该商家购物车
        if (cart==null){
            //4.1创建新的购物车对象
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());

            //创建购物车明细
            List<TbOrderItem> orderItemList=new ArrayList<>();//创建购物车列表
            TbOrderItem orderItem=createOreder(item,num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);

            //4.2将新的购物车对象添加购物车列表
            cartList.add(cart);
        }else{
            //5.如果购物车列表存在该商家的购物车
            //判断该商品是否在该购物车明细列表中存在
            TbOrderItem orderItem = searchOrderByItemId(cart.getOrderItemList(), itemId);
            if (orderItem==null){ //5.1不存在创建新的明细对象 并添加该购物车明细列表
                orderItem=createOreder(item,num);
                cart.getOrderItemList().add(orderItem);

            }else{ //5.2存在 在原有数量上增加数量 更新金额

                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //明细数量小于等于0
                if (orderItem.getNum()<=0){
                    cart.getOrderItemList().remove(orderItem);
                }
                if (cart.getOrderItemList().size()==0){
                    cartList.remove(cart);
                }
            }

        }

        return cartList;
    }




    @Override
    public List<Cart> findCartListFormRedis(String username) {
        System.out.println("从Redis提取购物车数据......"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向Redis存入购物车数据......"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList, List<Cart> cartList2) {
        //cartList.addAll(cartList2);//不能简单合并

        for (Cart cart: cartList2){
            for (TbOrderItem orderItem:cart.getOrderItemList()){
                addGoodsToCartList(cartList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList;
    }

    @Override
    public List<Cart> deleteCartList(List<Cart> cartList,Long itemId,String name) {
        for (Cart cart:cartList) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderByItemId(orderItemList, itemId);
            cart.getOrderItemList().remove(orderItem);
            if (cart.getOrderItemList().size()==0){
                cartList.remove(cart);
            }
        }
        if (!name.equals("anonymousUser")) {
            redisTemplate.boundHashOps("cartList").delete(name);
            redisTemplate.boundHashOps("cartList").put(name, cartList);
        }
        return cartList;
    }


    //根据商家Id查询购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){
        for (Cart cart:cartList){
            if (cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;

    }

    //根据SKUID在购物车列表查询购物车明细
    private TbOrderItem searchOrderByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem:orderItemList){
            if (orderItem.getItemId().longValue()==itemId.longValue()){//Long比较的是地址 基本数据类型才可以==
                return orderItem;
            }
        }
        return null;
    }

    //创建一个明细
    private TbOrderItem createOreder(TbItem item,Integer num){

        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return orderItem;
    }



}
