package com.store.page.service.impl;


import com.store.mapper.TbGoodsDescMapper;
import com.store.mapper.TbGoodsMapper;
import com.store.mapper.TbItemCatMapper;
import com.store.mapper.TbItemMapper;
import com.store.page.service.ItemPageService;
import com.store.pojo.*;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Value("${pageDir}")
    private String pageDir;
    @Override
    public boolean genItemHtml(Long goodsId) {
        freemarker.template.Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            //创建数据模型
            Map dateModel=new HashMap<>();
            TbGoods goods=goodsMapper.selectByPrimaryKey(goodsId);
            dateModel.put("goods",goods);

            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dateModel.put("goodsDesc",goodsDesc);

            String name1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String name2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String name3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dateModel.put("itemCat1",name1);
            dateModel.put("itemCat2",name2);
            dateModel.put("itemCat3",name3);

            //读取SKU列表
            TbItemExample example=new TbItemExample();
            TbItemExample.Criteria criteria=example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            example.setOrderByClause("is_default desc");//按照是否默认降序排序 目的返回的是结果第一条默认SKU

            List<TbItem> items = itemMapper.selectByExample(example);
            System.out.println("============="+items.size());
            dateModel.put("itemList",items);

            Writer out=new FileWriter(pageDir+goodsId+".html");



            template.process(dateModel,out);//输出

            out.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    //删除商品详细页
    @Override
    public boolean deleteItemHtml(Long[] Ids) {
        try {
            for (Long goodsId:Ids){
                new File(pageDir+goodsId+".html").delete();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }
}
