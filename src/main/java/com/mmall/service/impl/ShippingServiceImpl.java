package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Result;
import com.mmall.dao.ShippingMapper;
import com.mmall.domain.Shipping;
import com.mmall.domain.ShippingCriteria;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public Result add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return Result.success("新建地址成功",result);
        }
        return Result.error("新建地址失败");
    }

    public Result<String> del(Integer userId,Integer shippingId){
        ShippingCriteria shippingCriteria = new ShippingCriteria();
        ShippingCriteria.Criteria criteria = shippingCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andIdEqualTo(shippingId);
        int resultCount = shippingMapper.deleteByExample(shippingCriteria);
        if(resultCount > 0){
            return Result.success("删除地址成功");
        }
        return Result.error("删除地址失败");
    }


    public Result update(Integer userId, Shipping shipping){
        ShippingCriteria shippingCriteria = new ShippingCriteria();
        ShippingCriteria.Criteria criteria = shippingCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        int rowCount = shippingMapper.updateByExample(shipping,shippingCriteria);
        if(rowCount > 0){
            return Result.success("更新地址成功");
        }
        return Result.error("更新地址失败");
    }

    public Result<Shipping> select(Integer userId, Integer shippingId){
        ShippingCriteria shippingCriteria = new ShippingCriteria();
        ShippingCriteria.Criteria criteria = shippingCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andIdEqualTo(shippingId);
        List<Shipping> shippings = shippingMapper.selectByExample(shippingCriteria);
        if(shippings.size() == 0 ){
            return Result.error("无法查询到该地址");
        }
        return Result.success("更新地址成功",shippings.get(0));
    }


    public Result<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        ShippingCriteria shippingCriteria = new ShippingCriteria();
        ShippingCriteria.Criteria criteria = shippingCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Shipping> shippingList = shippingMapper.selectByExample(shippingCriteria);
        PageInfo pageInfo = new PageInfo(shippingList);
        return Result.success(pageInfo);
    }



}
