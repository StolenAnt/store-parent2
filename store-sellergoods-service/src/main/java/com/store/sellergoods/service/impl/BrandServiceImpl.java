package com.store.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.store.mapper.TbBrandMapper;
import com.store.pojo.TbBrand;
import com.store.pojo.TbBrandExample;
import com.store.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private TbBrandMapper BrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return BrandMapper.selectByExample(null);
    }

    @Override
    public List<TbBrand> findOne11(String name) {

        System.out.println(name+"`````````````````");
        List<TbBrand> list=BrandMapper.selectBrandBoolean(name);
        System.out.println(list.size());
        return list;
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        //当前页数以及一页多少条数据
        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page=(Page<TbBrand>)BrandMapper.selectByExample(null);

        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public Result add(TbBrand brand) {
//        String name=brand.getName();
//        System.out.println(name+"`````````````````");
//        List<TbBrand> list=BrandMapper.selectBrandBoolean(name);

        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria=example.createCriteria();
        criteria.andNameEqualTo(brand.getName());
        int cout=BrandMapper.countByExample(example);
        System.out.println(cout);
        if (cout==0) {
            BrandMapper.insert(brand);
            return new Result(true,"存入成功");
        }else{
            return new Result(false,"已经有一条===="+brand.getName()+"====存入失败");

        }
    }

    /*

    修改

     */

    @Override
    public TbBrand findOne(Long id) {
        return BrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        BrandMapper.updateByPrimaryKey(brand);
    }

    /*

        删除
     */

    @Override
    public void deleteOne(Long id) {
        BrandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id:ids){
            BrandMapper.deleteByPrimaryKey(id);
        }
    }


    /*

    搜索

     */
    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        TbBrandExample example=new TbBrandExample();
        TbBrandExample.Criteria criteria=example.createCriteria();
        if (brand!=null){
            if (brand.getName()!=null&&brand.getName().length()>0){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            if (brand.getFirstChar()!=null&&brand.getFirstChar().length()>0){
                criteria.andFirstCharLike("%"+brand.getFirstChar()+"%");
            }
        }
        Page<TbBrand> page=(Page<TbBrand>)BrandMapper.selectByExample(example);

        return new PageResult(page.getTotal(),page.getResult());


    }

    @Override
    public List<Map> selectOptionList() {
        return BrandMapper.selectOptionList();
    }


}
