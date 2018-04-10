package com.mmall.service;

import com.mmall.common.Result;
import com.mmall.domain.Category;

import java.util.List;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-10 11:38
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public interface ICategoryService {
    /**
     * 增加类目
     * @param categoryName
     * @param parentId
     * @return
     */
    Result addCategory(String categoryName,Integer parentId);

    /**
     * 修改类目名称
     * @param categoryName
     * @param categoryId
     * @return
     */
    Result<String> setCategoryName(String categoryName,Integer categoryId);

    /**
     * 获取类目列表 平级
     * @param categoryId
     * @return
     */
    Result<List<Category>> getCategoryList(Integer categoryId);

    /**
     * 获取类目列表递归
     * @param categoryId
     * @return
     */
    Result<List<Category>> getCategoryDeepList(Integer categoryId);

}
