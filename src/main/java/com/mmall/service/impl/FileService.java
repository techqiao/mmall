package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 15:30
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
@Slf4j
public class FileService implements IFileService {

    public String upload(MultipartFile file, String path) {
        //原始文件名
        String fileName = file.getOriginalFilename();
        //abc.jpg --> jpg 扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);

        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        log.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);
        //声明文件的本地目录
        File fileDir = new File(path);
        if(!fileDir.exists()){//判断目录是否存在
            fileDir.setWritable(true);//设置可写权限
            fileDir.mkdirs();//
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);
        try {
            // 文件上传到本地目录文件夹下
            file.transferTo(targetFile);
            // 将targetFile上传到FTP服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 将本地文件夹下的文件删除
            targetFile.delete();
        } catch (IOException e) {
            log.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
