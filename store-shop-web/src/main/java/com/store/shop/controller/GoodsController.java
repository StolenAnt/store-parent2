package com.store.shop.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;

import com.store.pojo.TbGoods;
import com.store.pojo.TbItem;
import com.store.pojogroup.Goods;

import com.store.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queueSolrDestination;

	@Autowired
	private Destination queueSolrDeleteDestination;

	@Autowired
	private Destination topicGoodsDestination;

	@Autowired
	private Destination topicGoodsDeleteDestination;

//	@Reference
//	private ItemSearchService itemSearchService;

//	@Reference
//	private ItemPageService itemPageService;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
		goods.getGoods().setSellerId(sellerId);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//判断商品是否是该商家的商品
		String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
		Goods goods2=goodsService.findOne(goods.getGoods().getId());
		String sellerGoodId=goods2.getGoods().getSellerId();

		if (!sellerGoodId.equals(sellerId)||!goods.getGoods().getSellerId().equals(sellerId)){
			return new Result(false,"非法操作");
		}else {
			try {
				goodsService.update(goods);
				return new Result(true, "修改成功");
			} catch (Exception e) {
				e.printStackTrace();
				return new Result(false, "修改失败");
			}
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);

//			//从索引库删除
//			itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			//删除页面 放入消息队列
            jmsTemplate.send(topicGoodsDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(ids);
                }
            });

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){

		String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);
	}

	@RequestMapping("/updateStatue")
	public void updateStatue(final Long id, String status){

		goodsService.UpdateMarkStatus(id,status);

		//更新Solr
		if (status.equals("1")) {
			 List<TbItem> item = goodsService.findItemListByGoodsIdListAndStatus(id, status);
//			//导入solr
//			itemSearchService.importList(item);

			final String jsonString = JSON.toJSONString(item);
			//发消息 传输对象必须是实现了序列化接口

			jmsTemplate.send(queueSolrDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(jsonString);
				}
			});

			System.out.println("消息发送完毕------");


			//创建页面
//			itemPageService.genItemHtml(id);
			jmsTemplate.send(topicGoodsDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {

					return session.createTextMessage(id+"");
				}
			});
		}else{
			//下架以后再创建
//			itemPageService.genItemHtml(id);
            jmsTemplate.send(topicGoodsDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {

                    return session.createTextMessage(id+"");
                }
            });
		}

	}

//	@RequestMapping("/test")
//	public void genHtml(Long id){
//		itemPageService.genItemHtml(id);
//	}



}
