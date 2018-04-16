package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Result;
import com.mmall.domain.Shipping;


public interface IShippingService {

    Result add(Integer userId, Shipping shipping);
    Result<String> del(Integer userId, Integer shippingId);
    Result update(Integer userId, Shipping shipping);
    Result<Shipping> select(Integer userId, Integer shippingId);
    Result<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
