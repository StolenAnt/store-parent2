package com.store.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.store.cart.service.CartService;
import com.store.pojo.TbOrder;
import com.store.pojo.TbOrderItem;
import com.store.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
//        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");//此方法不需要操作Cookie
//        response.setHeader("Access-Control-Allow-Credentials","true");//如果操作Cookie 必须有



        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录名
        System.out.println("当前登录人:"+name);


        try {
            //1.从cookie中取购物车列表
            List<Cart> cartList = findCartList();
            //2.调用服务方法 操作购物车
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            //判断是否登录
            if (name.equals("anonymousUser")){
                System.out.println("存入Cookie购物车.....");
                String cart=JSON.toJSONString(cartList);
                //3.将新的购物车存入cookie
                CookieUtil.setCookie(request,response,"carList",cart,3600*24,"UTF-8");

            }else{
                System.out.println("存入Redis购物车.....");
                cartService.saveCartListToRedis(name,cartList);
            }

            return new Result(true,"存入Cookie成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"存入cookie失败了");
        }



    }


    //从cookie取
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录名
        System.out.println("当前登录人:"+name);

        System.out.println("从Cookie提取购物车.....");
        String carListValue = CookieUtil.getCookieValue(request, "carList", "UTF-8");
        if (carListValue==null||carListValue.equals("")){
            carListValue="[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(carListValue, Cart.class);

        if (name.equals("anonymousUser")){ //未登录

            return cartList_cookie;
        }else{
            //已经登录
            System.out.println("从Redis提取购物车.....");
            List<Cart> cartList_redis = cartService.findCartListFormRedis(name);

            //判断本地购物车存在的数据
            if (cartList_cookie.size()>0) {
                //合并购物车
                List<Cart> carts = cartService.mergeCartList(cartList_cookie, cartList_redis);

                //存入Redis
                System.out.println("合并以后的购物车存入Redis.....");
                cartService.saveCartListToRedis(name, carts);
                //删除本地购物车
                CookieUtil.deleteCookie(request, response, "carList");
                return carts;
            }

            return cartList_redis;
        }
    }
    @RequestMapping("/delete")
    public List<Cart> deleteCartList(Long itemId){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录名
        List<Cart> cartList = findCartList();
        List<Cart> carts = cartService.deleteCartList(cartList, itemId, name);
        return carts;
    }
}
