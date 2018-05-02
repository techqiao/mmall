package com.mmall.controller.common.interceptor;

import com.mmall.domain.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-27 14:49
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
public class UserUtils {
    public UserUtils() {
    }

    public static User getUser(HttpServletRequest request) {
        return getUser(request, 1);
    }

    public static User getUser(HttpServletRequest request, Integer defaultUserId) {
        String debug = request.getParameter("debug");
        if (debug != null && debug.equals("true")) {
            User user = new User();
            user.setPhone("18612238981");
            user.setId(defaultUserId);
            user.setUsername("wujiangqiao");
            return user;
        } else {
            String loginToken = CookieUtil.readLoginToken(request);
            return JsonUtil.stringToObj(loginToken, User.class);
        }
    }
}
