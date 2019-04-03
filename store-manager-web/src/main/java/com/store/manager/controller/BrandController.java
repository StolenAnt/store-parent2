package com.store.manager.controller;


import java.util.List;
import java.util.Map;

import entity.PageResult;
import entity.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.store.pojo.TbBrand;
import com.store.sellergoods.service.BrandService;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;


    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(int page,int size){
        return brandService.findPage(page,size);

    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {

            return brandService.add(tbBrand);
        }catch (Exception e){
            return new Result(false,"失败");
        }
    }


    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {

            brandService.update(tbBrand);
            return new Result(true,"成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    /*

    删除


     */

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/deleteOne")
    public Result deleteOne(Long id){
        try {
            brandService.deleteOne(id);
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }


    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,int page,int size){
       return brandService.findPage(brand,page,size);

    }


    @RequestMapping("/selectOption")
    public List<Map> selectOption(){
        return brandService.selectOptionList();
    }

    @RequestMapping(value = "/1",produces = "application/json;charset=utf-8")
    public List<TbBrand> findOne(String name){

            return brandService.findOne11(name);
    }




}
