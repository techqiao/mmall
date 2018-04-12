package com.mmall.common;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 09:19
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Login {
}
