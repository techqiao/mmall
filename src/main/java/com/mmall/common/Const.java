package com.mmall.common;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-09 13:40
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USER_NAME = "username";
    public static final String TOKEN_PREFIX = "token_";
    public interface Role {
        int ROLE_CUSTOMER = 0;//普通用户
        int ROLE_ADMIN = 1;//管理员
    }
}
