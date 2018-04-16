package com.mmall.controller;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.domain.Shipping;
import com.mmall.domain.User;
import com.mmall.service.IShippingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@RequestMapping(value = "shipping", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "收货地址服务", description = "收货地址服务")
public class ShippingController {


    @Autowired
    private IShippingService iShippingService;


    @PostMapping("add")
    public Result add(HttpSession session, @RequestBody Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return iShippingService.add(user.getId(), shipping);
    }


    @DeleteMapping("del")
    public Result del(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return iShippingService.del(user.getId(), shippingId);
    }

    @PutMapping("update")
    public Result update(HttpSession session, @RequestBody Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return iShippingService.update(user.getId(), shipping);
    }


    @GetMapping("select")
    public Result<Shipping> select(HttpSession session, Integer shippingId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return iShippingService.select(user.getId(), shippingId);
    }


    @GetMapping("list")
    public Result<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                 HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return iShippingService.list(user.getId(), pageNum, pageSize);
    }


}
