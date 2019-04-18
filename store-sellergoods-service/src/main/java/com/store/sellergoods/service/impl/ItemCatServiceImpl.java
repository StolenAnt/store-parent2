package com.store.sellergoods.service.impl;
import java.util.List;

import com.store.mapper.TbItemCatMapper;
import com.store.pojo.TbItemCat;
import com.store.pojo.TbItemCatExample;
import com.store.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize,Long id) {
		TbItemCatExample example=new TbItemCatExample();
		TbItemCatExample.Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(id);
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
		redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
		System.out.println("将模板Id放入缓存.......");
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
		itemCatMapper.updateByPrimaryKey(itemCat);
		System.out.println("缓存更新了模板Id.....");
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			itemCatMapper.deleteByPrimaryKey(id);

			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(id);
			redisTemplate.boundHashOps("itemcat").delete(itemCat.getName());
			System.out.println("缓存清空了模板Id.....");
		}		
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		TbItemCatExample.Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findByParentId(Long id) {
		TbItemCatExample example=new TbItemCatExample();
		TbItemCatExample.Criteria criteria=example.createCriteria();
		criteria.andParentIdEqualTo(id);

//		//将模板ID放入缓存 (商品分类名称作为Key)
//
//        List<TbItemCat> itemCatList = findAll();
//        for (TbItemCat itemCat:itemCatList){
//				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
//
//			}
//			System.out.println("执行完毕.......");


        return itemCatMapper.selectByExample(example);
	}

}
