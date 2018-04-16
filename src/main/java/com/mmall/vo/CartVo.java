package com.mmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Description : 购物车VO
 * <p>Date : 2018-04-13 11:06
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;//是否已经都勾选
    private String imageHost;
}
