package com.mmall.common;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Description : 分页类
 * <p>Date : 2018-04-11 15:18
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Getter
@Setter
@Data
public class PagerParam {
    //当前页
    private Integer pageNum = 1;
    //每页条数
    private Integer pageSize = 10;
}
