package com.mmall.controller.common.interceptor;

import com.mmall.common.Login;
import com.mmall.common.UserException;
import com.mmall.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-27 14:47
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Slf4j
//@Component
public class UserAccessInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = UserUtils.getUser(request);
        log.info("用户信息放入request.attribute, {}", user);
        request.setAttribute("Attr_U_W_A", user);
        this.check(user, request, handler);
        return true;
    }

    private void check(User user, HttpServletRequest request, Object handler) {
        Login login = this.findAccess(handler);
        log.info("检查方法是否需要登录 {}", login != null);
        if (login != null && user == null && user == null) {
            log.info("用户没有登录，且没有设置UnLogin.");
            throw new UserException("无权限操作");
        }
    }

    private Login findAccess(Object handler) {
        Login annotation = null;
        if (handler instanceof HandlerMethod) {
            Method handlerMethod = ((HandlerMethod) handler).getMethod();
            annotation = AnnotationUtils.getAnnotation(handlerMethod, Login.class);
            if (annotation == null) {
                Class<?> clazz = handlerMethod.getDeclaringClass();
                annotation = AnnotationUtils.findAnnotation(clazz, Login.class);
                if (annotation == null) {
                    Package pg = clazz.getPackage();
                    annotation = pg.getAnnotation(Login.class);
                }
            }
        } else {
            annotation = AnnotationUtils.findAnnotation(handler.getClass(), Login.class);
        }

        return annotation;
    }
}
