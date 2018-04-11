package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.PagerParam;
import com.mmall.common.Result;
import com.mmall.domain.ProductWithBLOBs;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-11 14:41
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface IProductService {
    /**
     * 新增或修改商品
     * @param product
     * @return
     */
    Result saveOrUpdateProduct(ProductWithBLOBs product);

    /**
     * 商品分页
     * @param pagerParam
     * @return
     */
    Result<PageInfo> getProductList(PagerParam pagerParam);
}
