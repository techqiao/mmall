package com.mmall.controller.common;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.annotation.WebFilter;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-26 13:53
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Component
@WebFilter(urlPatterns = "/*")
@Order(1)
public class SpringSessionFilter extends DelegatingFilterProxy {

}
