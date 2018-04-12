package com.mmall.common;

import com.mmall.domain.User;
import com.mmall.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-12 09:20
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Aspect
@Component
@Slf4j
public class LoginAspect {
    @Autowired
    private IUserService userService;

    @Pointcut(value = "@annotation(com.mmall.common.Login)")
    public void HttpAspect() {
    }

    @Before("HttpAspect()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        //读取session中的用户
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        //url
        log.info("url={}", request.getRequestURL());
        //method
        log.info("method={}", request.getMethod());
        //ip
        log.info("ip={}", request.getRemoteAddr());
        //类方法
        log.info("class_method={}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        //参数
        log.info("method={}", joinPoint.getArgs());
        if (user == null) {
            throw new UserException("用户未登录");
        } else {
            if (!userService.checkAdminRole(user).isSuccess()) {
                throw new UserException("无权限操作");
            }
        }
    }
}
