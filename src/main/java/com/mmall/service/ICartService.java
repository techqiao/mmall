package com.mmall.service;

import com.mmall.common.Result;
import com.mmall.vo.CartVo;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-13 10:50
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface ICartService {
    Result add(Integer userId,Integer productId,Integer count);
    Result<CartVo> update(Integer userId, Integer productId, Integer count);
    Result<CartVo> deleteProduct(Integer userId,String productIds);

    Result<CartVo> list (Integer userId);
    Result<CartVo> selectOrUnSelect (Integer userId,Integer productId,Integer checked);
    Result<Integer> getCartProductCount(Integer userId);
}
