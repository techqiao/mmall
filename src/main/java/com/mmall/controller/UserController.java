package com.mmall.controller;

import com.mmall.common.Const;
import com.mmall.common.Result;
import com.mmall.domain.User;
import com.mmall.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>Description : 用户服务
 * <p>Date : 2018-04-09 10:45
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@RequestMapping(value = "user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "用户服务", description = "用户服务")
public class UserController {
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping("login")
    public Result<User> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Result<User> result = userService.login(username, password);
        if (result.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, result.getData());
        }
        return result;
    }

    @ApiOperation(value = "用户登出", notes = "用户登出")
    @GetMapping("loginOut")
    public Result<String> loginOut(HttpSession session) {
        return userService.loginOut(session);
    }

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping("register")
    public Result<String> register(@RequestBody User user) {
        return userService.register(user);
    }

    @ApiOperation(value = "用户参数校验", notes = "用户参数校验")
    @GetMapping("checkValid")
    public Result<String> checkValid(@RequestParam String value, @RequestParam String type) {
        return userService.checkValid(value, type);
    }

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @GetMapping("getUserInfo/{userId}")
    public Result<User> getUserInfo(@PathVariable Integer userId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return userService.getUserInfo(userId);
        }
        return Result.success(user);
    }

    @ApiOperation(value = "获取找回密码提示问题", notes = "获取找回密码提示问题")
    @GetMapping("selectQuestion")
    public Result<String> selectQuestion(@RequestParam String username) {
        return userService.selectQuestion(username);
    }

    @ApiOperation(value = "校验密码", notes = "校验密码")
    @PostMapping("checkAnswer")
    public Result<String> checkAnswer(@RequestBody User user) {
        return userService.checkAnswer(user);
    }

    @ApiOperation(value = "忘记密码的重置密码", notes = "忘记密码的重置密码")
    @PatchMapping("forgetResetPassword")
    public Result<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return userService.resetPassword(username, passwordNew, forgetToken);
    }


    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PatchMapping("updatePassword")
    public Result<String> updatePassword(HttpSession session, String passwordNew, String passwordOld) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return Result.error("用户未登录");
        }
        return userService.updatePassword(passwordNew, passwordOld, user);
    }

    @ApiOperation(value = "修改当前用户信息", notes = "修改当前用户信息")
    @PatchMapping("updateUserInfo")
    public Result<User> updateUserInfo(HttpSession session, @RequestBody User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return Result.error("用户未登录");
        }
        user.setId(currentUser.getId());
        Result<User> result = userService.updateUserInfo(user);
        if (result.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, result.getData());
        }
        return result;
    }


}
