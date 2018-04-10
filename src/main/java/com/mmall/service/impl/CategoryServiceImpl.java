package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.Result;
import com.mmall.dao.CategoryMapper;
import com.mmall.domain.Category;
import com.mmall.domain.CategoryCriteria;
import com.mmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-10 11:38
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isNotBlank(categoryName) || parentId == null) {
            return Result.error("添加品类错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        int count = categoryMapper.insert(category);
        if (count > 0) {
            return Result.success("添加品类成功");
        }
        return Result.error("添加品类失败");
    }

    @Override
    public Result<String> setCategoryName(String categoryName, Integer categoryId) {
        if (StringUtils.isNotBlank(categoryName) || categoryId == null) {
            return Result.error("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        category.setUpdateTime(new Date());
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if (count > 0) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");

    }

    @Override
    public Result<List<Category>> getCategoryList(Integer categoryId) {
        CategoryCriteria categoryCriteria = new CategoryCriteria();
        CategoryCriteria.Criteria criteria = categoryCriteria.createCriteria();
        criteria.andParentIdEqualTo(categoryId);
        List<Category> categoryList = categoryMapper.selectByExample(categoryCriteria);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("未找到当前分类的子分类");
        }
        return Result.success(categoryList);
    }

    @Override
    public Result<List<Category>> getCategoryDeepList(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);
        List<Category> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            for (Category category : categorySet) {
                categoryList.add(category);
            }
        }
        return Result.success(categoryList);
    }

    //递归算法 算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        CategoryCriteria categoryCriteria = new CategoryCriteria();
        CategoryCriteria.Criteria criteria = categoryCriteria.createCriteria();
        criteria.andParentIdEqualTo(categoryId);
        List<Category> categoryList = categoryMapper.selectByExample(categoryCriteria);
        //退出条件
        for (Category item : categoryList) {
            findChildCategory(categorySet, item.getId());
        }
        return categorySet;
    }
}
