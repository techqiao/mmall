package com.mmall.service;

import com.mmall.common.Result;

import java.util.Map;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-16 15:37
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface IOrderService {
    /**
     * 支付接口
     * @param orderNo
     * @param userId
     * @param path
     * @return
     */
    Result pay(Long orderNo, Integer userId, String path);

    /**
     * 回调接口
     * @param params
     * @return
     */
    Result aliCallback(Map<String,String> params);

    /**
     * 查询订单是否支付成功
     * @param userId
     * @param orderNo
     * @return
     */
    Result queryOrderPayStatus(Integer userId,Long orderNo);

}
