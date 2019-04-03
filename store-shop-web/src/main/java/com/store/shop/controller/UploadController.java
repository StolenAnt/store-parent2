package com.store.shop.controller;

import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {

    @Value("${FILE_SERVER-URL}")
    private String file_url;
    @RequestMapping("upload")
    public Result upload(MultipartFile file){
        String fileName=file.getOriginalFilename();
        String extName=fileName.substring(fileName.lastIndexOf(".")+1);
        try {
            FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId=client.uploadFile(file.getBytes(),extName);
            String url=file_url+fileId;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传出错");
        }
    }

    @RequestMapping("deleteFile")
    public Result deleteFile(MultipartFile file){
        String fileName=file.getOriginalFilename();
        String extName=fileName.substring(fileName.lastIndexOf(".")+1);
        try {
            FastDFSClient client=new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId=client.uploadFile(file.getBytes(),extName);
            String url=file_url+fileId;
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传出错");
        }
    }
}
