package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.PagerParam;
import com.mmall.common.Result;
import com.mmall.dao.ProductMapper;
import com.mmall.domain.Product;
import com.mmall.domain.ProductCriteria;
import com.mmall.domain.ProductWithBLOBs;
import com.mmall.service.IProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-11 14:41
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    public Result saveOrUpdateProduct(ProductWithBLOBs product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() != null) {
                int count = productMapper.updateByPrimaryKey(product);
                if (count > 0 ){
                    return Result.success("更新产品成功");
                }
                return Result.error("更新产品失败");
            } else {
                int count = productMapper.insert(product);
                if(count > 0) {
                    return Result.success("新增产品成功");
                }
                return Result.error("新增产品失败");
            }
        }
        return Result.error("产品参数错误");
    }

    @Override
    public Result<PageInfo> getProductList(PagerParam pagerParam) {
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pagerParam.getPageNum(),pagerParam.getPageSize(),true);
        ProductCriteria productCriteria = new ProductCriteria();
        productCriteria.setOrderByClause("category_id");
        List<Product> productList = productMapper.selectByExample(productCriteria);
        return Result.success(new PageInfo(productList));
    }
}
