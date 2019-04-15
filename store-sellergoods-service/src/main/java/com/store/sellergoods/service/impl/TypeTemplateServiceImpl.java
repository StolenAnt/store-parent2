package com.store.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.store.mapper.TbSpecificationOptionMapper;
import com.store.mapper.TbTypeTemplateMapper;
import com.store.pojo.TbSpecificationOption;
import com.store.pojo.TbSpecificationOptionExample;
import com.store.pojo.TbTypeTemplate;
import com.store.pojo.TbTypeTemplateExample;
import com.store.sellergoods.service.TypeTemplateService;
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
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		TbTypeTemplateExample.Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
						if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
			if(typeTemplate.getSpecIds()!=null && typeTemplate.getSpecIds().length()>0){
				criteria.andSpecIdsLike("%"+typeTemplate.getSpecIds()+"%");
			}
			if(typeTemplate.getBrandIds()!=null && typeTemplate.getBrandIds().length()>0){
				criteria.andBrandIdsLike("%"+typeTemplate.getBrandIds()+"%");
			}
			if(typeTemplate.getCustomAttributeItems()!=null && typeTemplate.getCustomAttributeItems().length()>0){
				criteria.andCustomAttributeItemsLike("%"+typeTemplate.getCustomAttributeItems()+"%");
			}
	
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);

		saveToRedis();
		//将缓存处理
		return new PageResult(page.getTotal(), page.getResult());
	}

	//放入缓存当中
	@Autowired
	private RedisTemplate redisTemplate;

	private void saveToRedis(){
		List<TbTypeTemplate> typeTemplateList = findAll();
		for (TbTypeTemplate tbTypeTemplate:typeTemplateList){
            List brandList1 = (List) redisTemplate.boundHashOps("brandList").get(tbTypeTemplate.getId());
            List specList1 = (List) redisTemplate.boundHashOps("specList").get(tbTypeTemplate.getId());
            if (brandList1==null||specList1==null) {
                //得到品牌
                List brandList = JSON.parseArray(tbTypeTemplate.getBrandIds(), Map.class);
                //System.out.println(brandList);
                redisTemplate.boundHashOps("brandList").put(tbTypeTemplate.getId(), brandList);

                //得到规格
                List<Map> specList = findSpecList(tbTypeTemplate.getId());
                redisTemplate.boundHashOps("specList").put(tbTypeTemplate.getId(), specList);
            }else {
                return;
            }

		}
		System.out.println("品牌列表...模板放入缓存......");
	}
	@Override
	public List<Map> selectOptionList() {
		return typeTemplateMapper.selectOptionList();
	}


	//去规格列表 在Map中增加一个options选项
	@Autowired
	private TbSpecificationOptionMapper tbSpecificationOptionMapper;
	@Override
	public List<Map> findSpecList(Long id) {
		//根据Id查询到模板对象
		TbTypeTemplate tbTypeTemplate=typeTemplateMapper.selectByPrimaryKey(id);
		//获得规格数据 spec_id 转换一下
		List<Map> list=JSON.parseArray(tbTypeTemplate.getSpecIds(),Map.class);
		for (Map map:list){
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria=example.createCriteria();
			criteria.andSpecIdEqualTo(new Long((Integer)map.get("id")));
			List<TbSpecificationOption> options=tbSpecificationOptionMapper.selectByExample(example);
			map.put("options",options);
		}
		return list;
	}

}
