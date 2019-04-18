package com.store.page.service;

public interface ItemPageService {


    //生成商品详细页面
    public boolean genItemHtml(Long goodsId);

    public boolean deleteItemHtml(Long[] Ids);
}
