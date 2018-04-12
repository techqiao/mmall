package com.mmall.common;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 10:51
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class UserException extends RuntimeException{
    public UserException(){
        super();
    }
    public UserException(String msg){
        super(msg);
    }
}
