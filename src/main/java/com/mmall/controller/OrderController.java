package com.mmall.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.Result;
import com.mmall.domain.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>Description : 订单服务
 * <p>Date : 2018-04-16 14:53
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@RestController
@RequestMapping(value = "order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "订单服务", description = "订单服务")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService orderService;


    @ApiOperation(value = "创建订单")
    @PostMapping("create")
    public Result create(HttpServletRequest request, Integer shippingId){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return orderService.createOrder(user.getId(),shippingId);
    }

    @ApiOperation(value = "取消订单")
    @PostMapping("cancel")
    public Result cancel(HttpServletRequest request, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return orderService.cancel(user.getId(),orderNo);
    }

    @ApiOperation(value = "获取购物车中已经选中的商品")
    @GetMapping("get_order_cart_product")
    public Result getOrderCartProduct(HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderCartProduct(user.getId());
    }


    @ApiOperation(value = "获取订单详情")
    @GetMapping("detail")
    public Result detail(HttpServletRequest request,Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderDetail(user.getId(),orderNo);
    }

    @ApiOperation(value = "订单列表")
    @GetMapping("list")
    public Result list(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") int pageNum, @RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }












    @ApiOperation(value = "支付接口")
    @PostMapping("pay")
    public Result pay(Long orderNo, HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if (user == null) {
            return Result.success(ResponseCode.NEED_LOGIN);
        }

        String path = request.getSession().getServletContext().getRealPath("upload");

        return orderService.pay(orderNo, user.getId(), path);
    }

    @ApiOperation(value = "支付宝回调，支付宝调用，需要外网")
    @PostMapping("alipay_callback")
    public Object alipayCallback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();//key
            String[] values = (String[]) requestParams.get(name);//value
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,标记sign:{},交易状态trade_status:{},参数params:{}", params.get("sign"), params.get("trade_status"), params.toString());

        //验证回调的正确性,是不是支付宝发的.并且避免重复通知.
        //1.在通知返回参数列表中，除去sign、sign_type两个参数外，凡是通知返回回来的参数皆是待验签的参数
        //2.将签名参数（sign）使用base64解码为字节码串
        //3.使用RSA2的验签方法，通过签名字符串、签名参数（经过base64解码）及支付宝公钥验证签名
        params.remove("sign_type");
        try {
            // 参数 公钥 字符集 RAS2签名类型
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {//非法请求
                return Result.error("非法请求,验证不通过,恶意请求");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常", e);
        }
        // TODO 验证各种数据
        // 商户需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        // 并判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        // 同时需要校验通知中的seller_id（或者seller_email)
        // 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email），上述有任何一个验证不通过，
        // 则表明本次通知是异常通知，务必忽略。在上述验证通过后商户必须根据支付宝不同类型的业务通知，
        // 正确的进行不同的业务处理，并且过滤重复的通知结果数据。在支付宝的业务通知中，
        // 只有交易通知状态为TRADE_SUCCESS或TRADE_FINISHED时，支付宝才会认定为买家付款成功。
        Result result = orderService.aliCallback(params);
        if (result.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    @ApiOperation(value = "查询该订单是否支付成功")
    @GetMapping("query_order_pay_status.do")
    public Result<Boolean> queryOrderPayStatus(HttpServletRequest request, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return Result.error("用户未登录");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.stringToObj(userJsonStr, User.class);
        if(user ==null){
            return Result.error(ResponseCode.NEED_LOGIN);
        }
        Result result = orderService.queryOrderPayStatus(user.getId(),orderNo);
        if(result.isSuccess()){
            return Result.success(true);
        }
        return Result.error("支付未成功");
    }
}
