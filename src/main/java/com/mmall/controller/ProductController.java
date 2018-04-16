package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.*;
import com.mmall.domain.Product;
import com.mmall.domain.ProductWithBLOBs;
import com.mmall.domain.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>Description : 产品服务
 * <p>Date : 2018-04-11 14:38
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@RequestMapping(value = "product", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "产品服务", description = "产品服务")
public class ProductController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IProductService productService;
    @Autowired
    private IFileService fileService;


    @ApiOperation(value = "商品更新", notes = "商品更新")
    @PostMapping("productSave")
    public Result productSave(HttpSession session, ProductWithBLOBs product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error("用户未登录");
        }
        if (userService.checkAdminRole(user).isSuccess()) {
            return productService.saveOrUpdateProduct(product);
        } else {
            return Result.error("无权限");
        }
    }


    @Login
    @ApiOperation(value = "商品列表", notes = "商品列表")
    @PostMapping("productList")
    public Result<PageInfo> list(@RequestBody PagerParam pagerParam) {
        return productService.getProductList(pagerParam);
    }

    //动态查询
    @Login
    @ApiOperation(value = "商品搜索", notes = "商品搜索")
    @PostMapping("searchProduct")
    public Result<Product> searchProduct(@RequestParam String name) {
        return productService.getProductDetail(name);
    }

    @Login
    @ApiOperation(value = "上传图片", notes = "上传图片")
    @PostMapping("upload")
    public Result upload(MultipartFile file, HttpServletRequest request) {
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = fileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        return Result.success(url);
    }

}
