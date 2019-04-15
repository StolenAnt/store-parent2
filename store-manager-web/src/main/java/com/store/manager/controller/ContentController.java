package com.store.manager.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.store.pojo.TbContent;
import com.store.content.service.ContentService;

import entity.PageResult;
import entity.Result;
import util.FastDFSClient;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/content")
public class ContentController {

	@Reference
	private ContentService contentService;


	private String getFileName(String f){

		char[] fileName=f.toCharArray();
		int j=0;
		for (int i=0;i<fileName.length;i++){
			if (fileName[i]=='8'){
				j=i+5;
				break;
			}
		}
		char[] file=new char[1000];
		for (int q=j,i=0;q<fileName.length;q++,i++){
			file[i]=fileName[q];
		}
		return new String(file);
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbContent> findAll(){			
		return contentService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return contentService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param content
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbContent content){
		try {
			contentService.add(content);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param content
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbContent content){
		try {
			contentService.update(content);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbContent findOne(Long id){
		return contentService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		TbContent content=new TbContent();

		try {
//			FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
//
//
//			for (Long id:ids){
//				content=contentService.findOne(id);
//				String name=content.getPic();
//				String name2=getFileName(name);
//				System.out.println("======"+name2);
//				client.delete_file(name2);
//			}
			contentService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	

	@RequestMapping("/search")
	public PageResult search(@RequestBody TbContent content, int page, int rows  ){
		return contentService.findPage(content, page, rows);		
	}
	
}
