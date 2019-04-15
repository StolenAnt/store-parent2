package com.store.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.store.pojo.TbItem;
import com.store.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout=5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map Search(Map searchMap) {
        Map map=new HashMap();

        //空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));//去掉空格
        /*
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        ScoredPage<TbItem> page=solrTemplate.queryForPage(query,TbItem.class);
        map.put("rows",page.getContent());
        */
        //1.查询列表
        map.putAll( searchList(searchMap));//putAll 追加上去
        //2.分组查询商品分类表
        List<String> categoryList = searchCategoryList(searchMap);
        map.put("categoryList",categoryList);

        //3.缓存中取品牌和规格列表
        String category = (String) searchMap.get("category");
        if (category.equals("")){
            if (categoryList.size()>0) {
                map.putAll(searchBrandAndSpecList(categoryList.get(0)));
            }
        }else{
            map.putAll(searchBrandAndSpecList(category));
        }

        return map;
    }


    //查询列表
    private Map searchList(Map searchMap){
        Map map=new HashMap();

        //高亮显示 HighlightQuery是 query的子接口
        HighlightQuery query=new SimpleHighlightQuery();
        //创建高亮选项
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        query.setHighlightOptions(highlightOptions);//为查询对象设置高亮选项


        //1.1关键字查询
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //1.2按照商品分类筛选过滤
        if (!"".equals(searchMap.get("category"))) {//选择了分类 筛选
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.3按照品牌筛选过滤
        if (!"".equals(searchMap.get("brand"))) {//选择了品牌 筛选
            FilterQuery filterQuery = new SimpleFacetQuery();
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        //1.4按照规格过滤
        if (!"".equals(searchMap.get("spec"))) {//选择了规格 筛选
            Map<String,String> specMap= (Map<String, String>) searchMap.get("spec");
            for (String key:specMap.keySet()){
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_spec_"+key).is(specMap.get(key));
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        //1.5价格过滤
        if (!"".equals(searchMap.get("price"))){

            String priceStr= (String) searchMap.get("price");
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){//起始价格不为0

                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){//最高价格不超过*
                FilterQuery filterQuery = new SimpleFacetQuery();
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                filterQuery.addCriteria(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6分页
        Integer pageNo=(Integer)searchMap.get("pageNo");//获取页码
        System.out.println("当前页码"+pageNo);
        if (pageNo==null){
            pageNo=1;
        }
        Integer pageSize=(Integer)searchMap.get("pageSize");
        System.out.println("当前页大小"+pageSize);
        if (pageSize==null){
            pageSize=20;
        }

        query.setOffset((pageNo-1)*pageSize);//起始索引
        query.setRows(pageSize);//每页记录数

        //1.7价格排序
        String sortValue = (String) searchMap.get("sort");//升序降序
        String sortField=(String) searchMap.get("sortField");
        if (sortValue!=null && !sortValue.equals("")){

            if (sortValue.equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+sortField);
                query.addSort(sort);
            }

        }



        //********************** 获取高亮结果集 **************************
        //高亮页对象
        HighlightPage<TbItem> page=solrTemplate.queryForHighlightPage(query,TbItem.class);
        //高亮入口集合(每条对象集合)
        List<HighlightEntry<TbItem>> entryList= page.getHighlighted();//获得高亮结果
        for (HighlightEntry<TbItem> entry:entryList){
            //获取高亮列表 取决于高亮域
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();
            /*
            for (HighlightEntry.Highlight h:highlightList){
                List<String> snipplets = h.getSnipplets();
            }
            */
            if (highlightList.size()>0&&highlightList.get(0).getSnipplets().size()>0) {
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",page.getContent());
        map.put("totalPage",page.getTotalPages());//总页数
        System.out.println("总页数"+page.getTotalPages());
        map.put("total",page.getTotalElements());//总记录数
        return map;
    }

    //分组查询商品分类列表
    private List<String> searchCategoryList(Map searchMap){
        List<String> list=new ArrayList();
        Query query=new SimpleQuery("*:*");

        //关键字查询 类似于Sql中的Where
        Criteria criteria=new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //分组选项
        GroupOptions groupOption=new GroupOptions().addGroupByField("item_category");//指定了分组 相当于 group By
        query.setGroupOptions(groupOption);

        //获取分组页面
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //获取分组结果对象
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //获取分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry:content){

            list.add(entry.getGroupValue());//分组结果添加返回值
        }
        return list;
    }

    //查询品牌和规格列表

    private Map searchBrandAndSpecList(String categoryName){
        Map map=new HashMap();
        //1.根据商品分类名称得到模板ID
        Long templatedId= (Long) redisTemplate.boundHashOps("itemCat").get(categoryName);
        if (templatedId!=null) {
            //2.根据模板Id获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templatedId);
            map.put("brandList", brandList);

            //3.根据模板Id获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templatedId);
            map.put("specList", specList);
        }
        return map;
    }

}
