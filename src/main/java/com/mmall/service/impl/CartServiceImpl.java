package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.domain.Cart;
import com.mmall.domain.CartCriteria;
import com.mmall.domain.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-13 10:51
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<CartVo> add(Integer userId, Integer productId, Integer count){
        if(productId == null || count == null){
            return Result.success(ResponseCode.ILLEGAL_ARGUMENT);
        }
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andProductIdEqualTo(productId);
        List<Cart> carts = cartMapper.selectByExample(cartCriteria);
        if(carts.size() != 0){
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else{
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = carts.get(0).getQuantity() + count;
            carts.get(0).setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(carts.get(0));
        }
        return this.list(userId);
    }

    @Override
    public Result<CartVo> update(Integer userId, Integer productId, Integer count){
        if(productId == null || count == null){
            return Result.success(ResponseCode.ILLEGAL_ARGUMENT);
        }
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andProductIdEqualTo(productId);
        List<Cart> carts = cartMapper.selectByExample(cartCriteria);
        Cart cart = carts.get(0);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        return this.list(userId);
    }

    @Override
    public Result<CartVo> deleteProduct(Integer userId, String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        List<Integer> products = Lists.newArrayList();
        for (String s : productList) {
            products.add(Integer.parseInt(s));
        }
        if(CollectionUtils.isEmpty(productList)){
            return Result.success(ResponseCode.ILLEGAL_ARGUMENT);
        }
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andProductIdIn(products);
        cartMapper.deleteByExample(cartCriteria);
        return this.list(userId);
    }


    @Override
    public Result<CartVo> list (Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);
        return Result.success(cartVo);
    }



    @Override
    public Result<CartVo> selectOrUnSelect (Integer userId, Integer productId, Integer checked){
//        cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    @Override
    public Result<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return Result.success(0);
        }
        return Result.success(cartMapper.countByExample(CreateExampleByUserId(userId)));
    }















    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectByExample(CreateExampleByUserId(userId));
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if(product != null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private CartCriteria CreateExampleByUserId(Integer userId) {
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return cartCriteria;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
//        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
        return true;
    }
}
