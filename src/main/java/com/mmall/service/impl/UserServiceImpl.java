package com.mmall.service.impl;

import com.mmall.common.CaffeineConfig;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.dao.UserMapper;
import com.mmall.domain.User;
import com.mmall.domain.UserCriteria;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-09 10:57
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result<User> login(String username, String password) {
        //检查用户名是否存在
        UserCriteria usercriteria = new UserCriteria();
        UserCriteria.Criteria criteria = usercriteria.createCriteria();
        criteria.andUsernameEqualTo(username);
        if (userMapper.countByExample(usercriteria) == 0) {
            return Result.error("用户名不存在");
        }
        criteria.andPasswordEqualTo(MD5Util.MD5EncodeUtf8(password));
        List<User> Users = userMapper.selectByExample(usercriteria);
        if (Users.size() == 0) {
            return Result.error(ResponseCode.NO_CHANGED);
        }
        User user = Users.get(0);
        user.setPassword(StringUtils.EMPTY);
        return Result.success("登录成功", user);
    }

    @Override
    public Result<String> loginOut(HttpSession session) {
//        session.removeAttribute(Const.CURRENT_USER);
        RedisPoolUtil.del(session.getId());
        return Result.success();
    }

    @Override
    public Result<String> register(User user) {
        //检查用户名是否存在
        Result<String> result = this.checkValid(user.getUsername(), Const.USER_NAME);
        if (!result.isSuccess()) {
            return result;
        }
        //校验Email是否存在
        Result<String> resultEmail = this.checkValid(user.getUsername(), Const.EMAIL);
        if (!resultEmail.isSuccess()) {
            return resultEmail;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        //MD5 加密
        int count = userMapper.insertSelective(user);
        if (count == 0) {
            return Result.error("注册失败");
        }
        return Result.success("注册成功");
    }

    @Override
    public Result<String> checkValid(String value, String type) {
        if (!StringUtils.isNotBlank(value)) {
            return Result.error("参数错误");
        } else {
            UserCriteria usercriteria = new UserCriteria();
            if (Const.USER_NAME.equals(type)) {
                UserCriteria.Criteria criteria = usercriteria.createCriteria();
                criteria.andUsernameEqualTo(value);
                if (userMapper.countByExample(usercriteria) > 0) {
                    return Result.error("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                usercriteria.createCriteria().andEmailEqualTo(value);
                if (userMapper.countByExample(usercriteria) > 0) {
                    return Result.error("Email已存在");
                }
            }
        }
        return Result.success("校验成功");
    }

    @Override
    public Result<User> getUserInfo(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user != null) {
            return Result.success(user);
        }
        return Result.success(user);
    }

    @Override
    public Result<String> selectQuestion(String username) {
        Result<String> result = this.checkValid(username, Const.USER_NAME);
        if (result.isSuccess()) {
            return Result.error("用户名不存在");
        }
        UserCriteria usercriteria = new UserCriteria();
        usercriteria.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(usercriteria);
        if (users.size() > 0) {
            return Result.success(users.get(0).getQuestion());
        }
        return Result.error(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Result<String> checkAnswer(User user) {
        UserCriteria usercriteria = new UserCriteria();
        UserCriteria.Criteria criteria = usercriteria.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        criteria.andPasswordEqualTo(user.getPassword());
        criteria.andQuestionEqualTo(user.getQuestion());
        int count = userMapper.countByExample(usercriteria);
        if (count > 0) {
            String forgetToken = UUID.randomUUID().toString();
            // forgetToken放入本地缓存,如果是集群情况下,那么forgetToken将拿不到，
            // 这里的forgetToken 是为了防止用户的横向越权
            // CaffeineConfig.setKey(Const.TOKEN_PREFIX + user.getUsername(), forgetToken);
            RedisPoolUtil.setEx(Const.TOKEN_PREFIX + user.getUsername(), forgetToken, 60 * 30);
            return Result.success(forgetToken);
        }
        return Result.error("问题的答案错误");
    }

    @Override
    public Result<String> resetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isNotBlank(forgetToken)) {
            return Result.error("参数错误,forgetToken");
        }
        Result<String> result = this.checkValid(username, Const.USER_NAME);
        if (result.isSuccess()) {
            return Result.error("用户名不存在");
        }
        //如果是集群情况下，那么forgetToken将拿不到,放入redis里面
        //String token = CaffeineConfig.getKey(Const.TOKEN_PREFIX + username);
        String token = RedisPoolUtil.get(Const.TOKEN_PREFIX + username);
        if (StringUtils.isNotBlank(token)) {
            return Result.error("token 无效已过期");
        }
        //从缓存里面拿到token前端传过来的验证
        if (StringUtils.equals(token, forgetToken)) {
            String password = MD5Util.MD5EncodeUtf8(passwordNew);
            UserCriteria usercriteria = new UserCriteria();
            UserCriteria.Criteria criteria = usercriteria.createCriteria();
            criteria.andUsernameEqualTo(username);
            User user = new User();
            user.setPassword(password);
            user.setUpdateTime(new Date());
            int count = userMapper.updateByExample(user, usercriteria);
            if (count > 0) {
                return Result.success("修改成功");
            }
        } else {
            return Result.error("token错误");
        }
        return Result.error("修改失败");
    }

    @Override
    public Result<String> updatePassword(String passwordNew, String passwordOld, User user) {
        UserCriteria usercriteria = new UserCriteria();
        UserCriteria.Criteria criteria = usercriteria.createCriteria();
        criteria.andIdEqualTo(user.getId());
        criteria.andPasswordEqualTo(MD5Util.MD5EncodeUtf8(passwordOld));
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        user.setUpdateTime(new Date());
        int count = userMapper.updateByExampleSelective(user, usercriteria);
        if (count > 0) {
            return Result.success("修改密码成功");
        }
        return Result.error("修改密码失败");
    }

    @Override
    public Result<User> updateUserInfo(User user) {
        //username 不能更改
        //email 校验，检验新的
        UserCriteria usercriteria = new UserCriteria();
        UserCriteria.Criteria criteria = usercriteria.createCriteria();
        criteria.andIdNotEqualTo(user.getId());
        criteria.andEmailEqualTo(user.getEmail());
        int count = userMapper.countByExample(usercriteria);
        if (count > 0) {
            return Result.error("Email 已经存在");
        }
        user.setUpdateTime(new Date());
        int count2 = userMapper.updateByPrimaryKey(user);
        if (count2 > 0) {
            return Result.success(user);
        }
        return Result.error("修改失败");
    }

    @Override
    public Result<Boolean> checkAdminRole(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
            return Result.success();
        }
        return Result.error();
    }
}
