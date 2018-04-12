package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.*;
import com.mmall.domain.Product;
import com.mmall.domain.ProductWithBLOBs;
import com.mmall.domain.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>Description : mmall
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

    @ApiOperation(value = "商品更新", notes = "商品更新")
    @PostMapping("productSave")
    public Result productSave(HttpSession session, ProductWithBLOBs product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.error("用户未登录");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            return productService.saveOrUpdateProduct(product);
        }else {
            return Result.error("无权限");
        }
    }


    @ApiOperation(value = "商品列表", notes = "商品列表")
    @PostMapping("productList")
    public Result<PageInfo> list(@RequestBody PagerParam pagerParam,HttpSession session){
//        User user = (User)session.getAttribute(Const.CURRENT_USER);
//        if(user==null){
//            return Result.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
//        }
//        if(userService.checkAdminRole(user).isSuccess()){
//            return productService.getProductList(pagerParam);
//        }else {
//            return Result.error("无权限");
//        }
        return productService.getProductList(pagerParam);
    }

    //动态查询


}
