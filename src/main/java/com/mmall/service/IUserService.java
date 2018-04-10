package com.mmall.service;

import com.mmall.common.Result;
import com.mmall.domain.User;

import javax.servlet.http.HttpSession;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-09 10:56
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface IUserService {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    Result<User> login(String username, String password);

    /**
     * 登出
     * @return
     */
    Result<String> loginOut(HttpSession session);

    /**
     * 注册
     * @param user
     * @return
     */
    Result<String> register(User user);

    /**
     * 校验参数
     * @param value
     * @param type
     * @return
     */
    Result<String> checkValid(String value, String type);

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    Result<User> getUserInfo(Integer userId);

    /**
     * 查询密码提示问题
     * @param username
     * @return
     */
    Result<String> selectQuestion(String username);

    /**
     * 校验验证密码答案是否正确
     * @param user
     * @return
     */
    Result<String> checkAnswer(User user);

    /**
     * 重置密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    Result<String> resetPassword(String username, String passwordNew, String forgetToken);

    /**
     * 修改密码
     * @param passwordNew
     * @param passwordOld
     * @return
     */
    Result<String> updatePassword(String passwordNew,String passwordOld,User user);


    /**
     * 修改当前用户信息
     * @param user
     * @return
     */
    Result<User> updateUserInfo(User user);

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    Result<Boolean> checkAdminRole(User user);
}
