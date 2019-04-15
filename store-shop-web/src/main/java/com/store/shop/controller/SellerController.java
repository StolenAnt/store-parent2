package com.store.shop.controller;
import java.util.List;

import com.store.pojo.TbSeller;
import com.store.sellergoods.service.SellerService;
import com.store.shop.service.UserDetailsServiceImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;


import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

	@Reference
	private SellerService sellerService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeller> findAll(){
		return sellerService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return sellerService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seller
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeller seller){
		//加密
		BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder();
		String password=passwordEncoder.encode(seller.getPassword());
		seller.setPassword(password);

		try {
			sellerService.add(seller);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seller
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeller seller){
		try {
			String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
			seller.setSellerId(sellerId);
			sellerService.update(seller);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbSeller findOne(String id){
			String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
			return sellerService.findOne(sellerId);
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(String [] ids){
		try {

			sellerService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页

	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeller seller, int page, int rows  ){
		return sellerService.findPage(seller, page, rows);		
	}

	@RequestMapping("/updatepass")
	public Result UpdatePassword(String oldpass,String newpass,String qpass){
		String sellerId=SecurityContextHolder.getContext().getAuthentication().getName();
		TbSeller seller = sellerService.findOne(sellerId);
		BCryptPasswordEncoder passwordEncoder =new BCryptPasswordEncoder();

		String npassword=passwordEncoder.encode(newpass);


		boolean matches = passwordEncoder.matches(oldpass, seller.getPassword());
		if (matches){
			if (newpass.equals(qpass)){
				sellerService.updatePassword(sellerId,npassword);
				return new Result(true,"修改成功");
			}else {
				return new Result(false,"两次密码不一致 修改失败");
			}
		}else {
			return new Result(false,"原密码不匹配,修改失败");
		}

	}
	
}
