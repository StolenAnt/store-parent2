package com.store.solrutil;

import com.alibaba.fastjson.JSON;
import com.store.mapper.TbItemMapper;
import com.store.pojo.TbItem;
import com.store.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void imporItemDate(){
        TbItemExample example=new TbItemExample();
        TbItemExample.Criteria criteria=example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过的导入
        List<TbItem> list=itemMapper.selectByExample(example);
//        System.out.println("-------商品列表------");

        for (TbItem item:list){
            System.out.println(item.getId()+"   "+item.getTitle()+"  "+item.getPrice());

            //[] 方括号 表示集合 可以用parseArray  { }开始表示对象 得用Object
            Map map= JSON.parseObject(item.getSpec(),Map.class);
            item.setSpecMap(map);
        }

//        System.out.println("-------结束------");


        //添加到Solr中
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    public static void main(String[] args){
        ApplicationContext context=new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil=(SolrUtil) context.getBean("solrUtil");
        solrUtil.imporItemDate();
    }
}
