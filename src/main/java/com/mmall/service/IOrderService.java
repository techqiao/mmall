package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Result;
import com.mmall.vo.OrderVo;

import java.util.Map;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-16 15:37
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface IOrderService {

    /**
     * 创建订单
     * @param userId
     * @param shippingId
     * @return
     */
    Result createOrder(Integer userId, Integer shippingId);

    /**
     * 取消订单
     * @param userId
     * @param orderNo
     * @return
     */
    Result<String> cancel(Integer userId, Long orderNo);


    /**
     * 获取购物车选中商品详情
     * @param userId
     * @return
     */
    Result getOrderCartProduct(Integer userId);

    /**
     * 获取订单详情
     * @param userId
     * @param orderNo
     * @return
     */
    Result<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    /**
     * 订单列表
     * @param userId
     * @param pageNum
     * @param pageSize
     * @return
     */
    Result<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

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

    /**
     * 关闭订单
     * @param hour
     */
    void closeOrder(int hour);

}
