package com.store.order.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.store.mapper.TbOrderItemMapper;
import com.store.mapper.TbPayLogMapper;
import com.store.order.service.OrderService;
import com.store.pojo.*;
import com.store.pojogroup.Cart;
import com.store.pojogroup.OrderGroup;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.store.mapper.TbOrderMapper;


import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private TbPayLogMapper payLogMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {
		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbOrder order) {
		//1.redis提取购物车列表
		List<Cart> cartList= (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		List<String> orderList=new ArrayList();
		double total_fee=0;
		//2.循环购物车列表添加订单
		for (Cart cart:cartList){
			TbOrder tbOrder=new TbOrder();
			long orderId=idWorker.nextId();
			tbOrder.setOrderId(orderId);
			tbOrder.setPaymentType(order.getPaymentType());
			tbOrder.setStatus("1");
			tbOrder.setCreateTime(new Date());
			tbOrder.setUpdateTime(new Date());
			tbOrder.setUserId(order.getUserId());
			tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货人地址
			tbOrder.setReceiver(order.getReceiver());//收货人
			tbOrder.setReceiverMobile(order.getReceiverMobile());//收件人电话
			tbOrder.setSourceType(order.getSourceType());//订单来源
			tbOrder.setSellerId(cart.getSellerId());//卖家Id

			double money=0;
			//循环购物车中每一条明细记录
			for (TbOrderItem orderItem:cart.getOrderItemList()){
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);
				orderItem.setSellerId(cart.getSellerId());
				orderItemMapper.insert(orderItem);
				money+=orderItem.getTotalFee().doubleValue();
			}
			tbOrder.setPayment(new BigDecimal(money));

			orderMapper.insert(tbOrder);

			orderList.add(orderId+"");
			total_fee+=money;
		}

		if (order.getPaymentType().equals("1")){//增加支付日志
			TbPayLog tbPayLog=new TbPayLog();
			tbPayLog.setOutTradeNo(idWorker.nextId()+"");
			tbPayLog.setCreateTime(new Date());
			tbPayLog.setUserId(order.getUserId());
			tbPayLog.setOrderList(orderList.toString().replace("[","").replace("]",""));
			tbPayLog.setTotalFee((long)(total_fee));//金额
			tbPayLog.setTradeState("0");
			payLogMapper.insert(tbPayLog);
			//放入缓存当中
			redisTemplate.boundHashOps("payLog").put(order.getUserId(),tbPayLog);
		}


		//3.清除redis中的购物车
		redisTemplate.boundHashOps("cartList")	.delete(order.getUserId());

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order){
		orderMapper.updateByPrimaryKey(order);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id){
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			orderMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbOrderExample example=new TbOrderExample();
		TbOrderExample.Criteria criteria = example.createCriteria();
		
		if(order!=null){			
						if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
				criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(order.getPostFee()!=null && order.getPostFee().length()>0){
				criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(order.getStatus()!=null && order.getStatus().length()>0){
				criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(order.getShippingName()!=null && order.getShippingName().length()>0){
				criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
				criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(order.getUserId()!=null && order.getUserId().length()>0){
				criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
				criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
				criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
				criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
				criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
				criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
				criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(order.getReceiver()!=null && order.getReceiver().length()>0){
				criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
				criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(order.getSourceType()!=null && order.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(order.getSellerId()!=null && order.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
		
		Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public TbPayLog searchPayLogFromRedis(String userId) {
		TbPayLog payLog= (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
		return payLog;
	}

	@Override
	public void updateOrderStatus(String out_trade_no, String transcation_id) {
		//1.修改支付日志
		TbPayLog tbPayLog = payLogMapper.selectByPrimaryKey(out_trade_no);
		tbPayLog.setPayTime(new Date());
		tbPayLog.setTradeState("1");//交易成功
		tbPayLog.setTransactionId(transcation_id);

		payLogMapper.updateByPrimaryKey(tbPayLog);

		//2.修改订单表状态
		String orderList = tbPayLog.getOrderList();
		String[] orderids = orderList.split(",");
		for (String id:orderids){
			TbOrder order = orderMapper.selectByPrimaryKey(Long.valueOf(id));
			order.setStatus("2");
			orderMapper.updateByPrimaryKey(order);
			System.out.println("=========订单修改状态"+order.getStatus());
		}

		//3.清除缓存中的payLog
		redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());

	}

	@Override
	public List<OrderGroup> findOrderListByUser(String user) {
		List<OrderGroup> orderGroupList=new ArrayList();

		TbOrderExample example=new TbOrderExample();
		TbOrderExample.Criteria criteria=example.createCriteria();
		criteria.andUserIdEqualTo(user);
		List<TbOrder> tbOrderList = orderMapper.selectByExample(example);
		for (TbOrder order:tbOrderList){
			OrderGroup orderGroup=new OrderGroup();
			orderGroup.setOrder(order);

			TbOrderItemExample example1=new TbOrderItemExample();
			TbOrderItemExample.Criteria criteria1=example1.createCriteria();
			criteria1.andOrderIdEqualTo(order.getOrderId());
			List<TbOrderItem> tbOrderItems = orderItemMapper.selectByExample(example1);

			orderGroup.setOrderItems(tbOrderItems);
			orderGroupList.add(orderGroup);
		}


		return orderGroupList;
	}

}
