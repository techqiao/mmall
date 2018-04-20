package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.domain.User;
import com.mmall.service.ICartService;
import com.mmall.vo.CartVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>Description : 购物车服务
 * <p>Date : 2018-04-13 10:45
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@Api(value = "购物车服务",description = "购物车服务")
@RequestMapping(value = "cart",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class CartController {

    @Autowired
    private ICartService iCartService;

    @GetMapping("list")
    public Result<CartVo> list(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.list(user.getId());
    }

    @PostMapping("add")
    public Result<CartVo> add(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.add(user.getId(),productId,count);
    }



    @PutMapping("update")
    public Result<CartVo> update(HttpSession session, Integer count, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.update(user.getId(),productId,count);
    }

    @DeleteMapping("delete_product")
    public Result<CartVo> deleteProduct(HttpSession session,String productIds){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }


    @GetMapping("select_all")
    public Result<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @GetMapping("un_select_all")
    public Result<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }



    @GetMapping("select")
    public Result<CartVo> select(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @GetMapping("un_select")
    public Result<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(ResponseCode.NEED_LOGIN);
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }



    @GetMapping("get_cart_product_count")
    public Result<Integer> getCartProductCount(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return Result.success(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }

}
