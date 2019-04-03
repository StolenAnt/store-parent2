package com.store.sellergoods.service;


/*
        品牌接口
 */

import com.store.pojo.TbAddress;
import com.store.pojo.TbBrand;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<TbBrand> findAll();
    public List<TbBrand> findOne11(String name);
//    public boolean BrandBoolean(String name);

    //分页
    public PageResult findPage(int pageNum,int pageSize);

    public Result add(TbBrand brand);

    //修改

    public TbBrand findOne(Long id);
    public void update(TbBrand brand);

    //删除
    public void deleteOne(Long id);
    public void delete(Long[] ids);

    //搜索分页
    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    //
    public List<Map> selectOptionList();


}
