package com.mmall.common;

import com.mmall.domain.User;
import com.mmall.service.IUserService;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
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

    @Around("HttpAspect()")
    //  @Before("HttpAspect()")
    //  如果要直接在controller自动拦截返回Result信息，需要用@Around,不然只能自己抛出异常处理或者写处理的接口类处理
    public Result doBefore(JoinPoint joinPoint) {
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
            //throw new UserException("用户未登录");
            return Result.error("用户未登录");
        } else {
            if (!userService.checkAdminRole(user).isSuccess()) {
//                throw new UserException("无权限操作");
                return Result.error("无权限操作");
            }
        }
        return null;
    }

    @AfterReturning(returning = "result", pointcut = "HttpAspect()")
    public void afterReturning(Result result) {
        log.info("response={}", result);
    }
}
