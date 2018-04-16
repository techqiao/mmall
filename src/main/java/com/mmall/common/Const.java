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

    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private String value;
        private int code;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}
