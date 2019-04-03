package com.store.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.store.mapper.TbSpecificationMapper;
import com.store.mapper.TbSpecificationOptionMapper;
import com.store.pojo.TbSpecification;
import com.store.pojo.TbSpecificationExample;
import com.store.pojo.TbSpecificationOption;
import com.store.pojo.TbSpecificationOptionExample;
import com.store.pojogroup.Specification;
import com.store.sellergoods.service.SpecificationService;

import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;


import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public Result add(Specification specification) {
		TbSpecificationExample tbSpecificationExample=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria=tbSpecificationExample.createCriteria();
		TbSpecification tbSpecification=specification.getSpecification();
		criteria.andSpecNameEqualTo(tbSpecification.getSpecName());
		int count=specificationMapper.countByExample(tbSpecificationExample);
		if (count==0) {
			specificationMapper.insert(tbSpecification);

			List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
			for (TbSpecificationOption option : specificationOptionList) {
				option.setSpecId(tbSpecification.getId());
				specificationOptionMapper.insert(option);
			}
			return new Result(true,"存入成功");
		}else{
			return new Result(false,"已经有一条===="+tbSpecification.getSpecName()+"====存入失败");
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Specification specification){

			TbSpecification tbSpecification = specification.getSpecification();
			specificationMapper.updateByPrimaryKey(tbSpecification);

			//删除原来的数据
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(tbSpecification.getId());
			specificationOptionMapper.deleteByExample(example);

			//获得规格选项集合
			List<TbSpecificationOption> list = specification.getSpecificationOptionList();
			for (TbSpecificationOption option : list) {
				option.setSpecId(tbSpecification.getId());
				specificationOptionMapper.insert(option);
			}


	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification=new Specification();
		TbSpecification tbSpecification=specificationMapper.selectByPrimaryKey(id);
		specification.setSpecification(tbSpecification);
		TbSpecificationOptionExample example=new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria=example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> list=specificationOptionMapper.selectByExample(example);
		specification.setSpecificationOptionList(list);
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			specificationMapper.deleteByPrimaryKey(id);
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria=example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);
		}		
	}

	@Override
	public void deleOne(Long id) {
		specificationMapper.deleteByPrimaryKey(id);
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria=example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		specificationOptionMapper.deleteByExample(example);
	}


	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		TbSpecificationExample.Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<Map> selectOptionList() {
		return specificationMapper.selectOptionList();
	}

}
