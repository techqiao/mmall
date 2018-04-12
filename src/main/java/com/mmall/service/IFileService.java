package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 15:30
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface IFileService {

    String upload(MultipartFile file, String path);
}
