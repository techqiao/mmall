package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.Result;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <p>Description : 用户服务
 * <p>Date : 2018-04-09 10:45
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@RequestMapping(value = "user/springSession", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "用户服务", description = "用户服务")
public class UserSpringSessionController {
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("login")
    public Result<User> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response, HttpSession session) {
        Result<User> result = userService.login(username, password);
        if (result.isSuccess()) {
            //session.setAttribute(Const.CURRENT_USER, result.getData());
            // 写入 cookie
            CookieUtil.writeLoginToken(response, session.getId());
            //CookieUtil.readLoginToken(request);
            //CookieUtil.delLoginToken(response, request);
            // 放入缓存
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.objToString(result.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            // 模拟集群 同一个用户在不同服务器上的session id不同
        }
        return result;
    }

    @ApiOperation(value = "用户登出", notes = "用户登出")
    @GetMapping("loginOut")
    public Result<String> loginOut(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        String loginToken = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(response, request);
        RedisShardedPoolUtil.del(loginToken);
        return Result.success();
    }


    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @GetMapping("getUserInfo")
    public Result<User> getUserInfo(HttpServletRequest request, HttpSession session) {
        session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if (user != null) {
            return Result.success(user);
        }
        return Result.success(user);
    }


}
