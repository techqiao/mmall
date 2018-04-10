package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.domain.Category;
import com.mmall.domain.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-10 11:28
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@RequestMapping(value = "category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "类目服务",description = "商品的类目服务")
public class CategoryController {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @ApiOperation(value = "增加类目")
    @PostMapping("addCategory")
    public Result addCategory(HttpSession session, String categoryName,
                              @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return Result.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            return categoryService.addCategory(categoryName,parentId);
        }else {
            return Result.error("无权限操作");
        }
    }


    @ApiOperation(value = "修改类目名称")
    @PostMapping("setCategoryName")
    public Result setCategoryName(HttpSession session, String categoryName, Integer categoryId) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return Result.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            return categoryService.setCategoryName(categoryName,categoryId);
        }else {
            return Result.error("无权限操作");
        }
    }

    @ApiOperation(value = "获取子节点类目,不递归")
    @GetMapping("getChildParableCategory")
    public Result<List<Category>> getChildParableCategory(HttpSession session,
                                                          @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return Result.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            return categoryService.getCategoryList(categoryId);
        }else {
            return Result.error("无权限操作");
        }
    }


    @ApiOperation(value = "获取子节点类目,递归")
    @GetMapping("getChildDeepCategory")
    public Result<List<Category>> getChildDeepCategory(HttpSession session,
                                                          @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return Result.error(ResponseCode.NEED_LOGIN.getCode(),"用户未登录");
        }
        if(userService.checkAdminRole(user).isSuccess()){
            //查询当前节点的id和递归子节点的id
            return categoryService.getCategoryDeepList(categoryId);
        }else {
            return Result.error("无权限操作");
        }
    }

}
