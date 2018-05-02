package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.Result;
import com.mmall.dao.*;
import com.mmall.domain.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * <p>Description : mmall
 * <p>Date : 2018-04-16 15:37
 * <p>@Author : wjq
 * <P>Email : wujiangqiao@difengshanguo.com
 */
@Service
public class OrderServiceImpl implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShippingMapper shippingMapper;

    @Transactional
    public Result createOrder(Integer userId, Integer shippingId) {
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andCheckedEqualTo(1);
        //从购物车中获取已被勾选的商品
        List<Cart> cartList = cartMapper.selectByExample(cartCriteria);
        //计算订单总价
        Result result = this.getCartOrderItem(userId, cartList);
        if (!result.isSuccess()) {
            return result;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) result.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);
        //生成订单
        Order order = this.assembleOrder(userId, shippingId, payment);
        if (order == null) {
            return Result.error("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderItemList)) {
            return Result.error("购物车为空");
        }
        //OrderItem 设置订单号
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        //订单详情 批量插入
        int count = orderItemMapper.batchInsert(orderItemList);
        if (count == 0) {
            return Result.error("数据库异常");
        }
        //生成成功,减少产品的库存
        this.reduceProductStock(orderItemList);
        //清空一下购物车
        this.cleanCart(cartList);
        //组装前端数据
        OrderVo orderVo = assembleOrderVo(order, orderItemList);
        return Result.success(orderVo);

    }


    public Result<String> cancel(Integer userId, Long orderNo) {
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andOrderNoEqualTo(orderNo);
        Order order = orderMapper.selectByExample(orderCriteria).get(0);
        if (order == null) {
            return Result.error("该用户此订单不存在");
        }
        if (order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return Result.error("已付款,无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    public Result getOrderCartProduct(Integer userId) {
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据
        CartCriteria cartCriteria = new CartCriteria();
        CartCriteria.Criteria criteria = cartCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Cart> cartList = cartMapper.selectByExample(cartCriteria);
        Result result = this.getCartOrderItem(userId, cartList);
        if (!result.isSuccess()) {
            return result;
        }
        List<OrderItem> orderItemList = (List<OrderItem>) result.getData();

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return Result.success(orderProductVo);
    }

    public Result<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria1 = orderCriteria.createCriteria();
        criteria1.andUserIdEqualTo(userId);
        criteria1.andOrderNoEqualTo(orderNo);
        Order order = orderMapper.selectByExample(orderCriteria).get(0);
        if (order != null) {
            OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
            criteria.andUserIdEqualTo(userId);
            criteria.andOrderNoEqualTo(orderNo);
            List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemCriteria);
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            return Result.success(orderVo);
        }
        return Result.error("没有找到该订单");
    }

    public Result<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria1 = orderCriteria.createCriteria();
        criteria1.andUserIdEqualTo(userId);
        List<Order> orderList = orderMapper.selectByExample(orderCriteria);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return Result.success(pageResult);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList, Integer userId) {
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList) {
            List<OrderItem> orderItemList;
            OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
            OrderItemCriteria.Criteria criteria = orderItemCriteria.createCriteria();
            criteria.andOrderNoEqualTo(order.getOrderNo());
            if (userId == null) {
                //管理员
                orderItemList = orderItemMapper.selectByExample(orderItemCriteria);
            } else {
                criteria.andUserIdEqualTo(userId);
                orderItemList = orderItemMapper.selectByExample(orderItemCriteria);
            }
            OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());

        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }

        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));


        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));


        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        for (OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        shippingVo.setReceiverPhone(shippingVo.getReceiverPhone());
        return shippingVo;
    }

    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            ProductWithBLOBs product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment) {
        Order order = new Order();
        long orderNo = this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(payment);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //发货时间 发货的时候更新
        //付款时间 付款的时候更新
        int rowCount = orderMapper.insert(order);
        if (rowCount > 0) {
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        //订单号生成 主键肯定不行 可以看到一天的订单量
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for (OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private Result getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return Result.success("购物车为空");
        }
        for (Cart cartItem : cartList) {
            ProductWithBLOBs product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验状态
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return Result.error("产品" + product.getName() + "不是在线售卖状态");
            }
            //校验库存
            if (cartItem.getQuantity() > product.getStock()) {
                return Result.error("产品" + product.getName() + "库存不足");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
        return Result.success(orderItemList);
    }


    public Result pay(Long orderNo, Integer userId, String path) {
        Map<String, String> resultMap = Maps.newHashMap();
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andOrderNoEqualTo(orderNo);
        List<Order> orderList = orderMapper.selectByExample(orderCriteria);
        Order order = orderList.get(0);
        if (order == null) {
            return Result.error("用户没有该订单");
        }
        resultMap.put("orderNo", String.valueOf(order.getOrderNo()));

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("techqiao测试扫码支付,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "订单" + outTradeNo + "购买商品共" + totalAmount + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";
        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<>();
        OrderItemCriteria orderItemCriteria = new OrderItemCriteria();
        OrderItemCriteria.Criteria criteria1 = orderItemCriteria.createCriteria();
        criteria1.andUserIdEqualTo(userId);
        criteria1.andOrderNoEqualTo(orderNo);
        List<OrderItem> orderItemList = orderItemMapper.selectByExample(orderItemCriteria);
        for (OrderItem orderItem : orderItemList) {
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().setCharset("utf-8").build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                //日志打印
                dumpResponse(response);
                File folder = new File(path);
                if (!folder.exists()) {
                    folder.setWritable(true);//设置写权限
                    folder.mkdirs();//创建目录
                }
                // 需要修改为运行机器上的路径
                // 细节细节细节 二维码路径
                String qrPath = String.format(path + "/" + "qr-%s.png", response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png", response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path, qrFileName);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常", e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFile.getName();
                resultMap.put("qrUrl", qrUrl);
                return Result.success(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return Result.error("支付宝预下单失败!!!");
            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return Result.error("系统异常，预下单状态未知!!!");
            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return Result.error("不支持的交易状态，交易返回异常!!!");
        }
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }


    public Result aliCallback(Map<String, String> params) {
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
        criteria.andOrderNoEqualTo(orderNo);
        Order order = orderMapper.selectByExample(orderCriteria).get(0);
        if (order == null) {
            return Result.error("非系统的订单,回调忽略");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return Result.success("支付宝重复调用");
        }
        if (Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)) {
            //交易付款时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //订单状态
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);//支付宝交易号
        payInfo.setPlatformStatus(tradeStatus);//订单状态

        payInfoMapper.insert(payInfo);

        return Result.success();
    }


    public Result queryOrderPayStatus(Integer userId, Long orderNo) {
        OrderCriteria orderCriteria = new OrderCriteria();
        OrderCriteria.Criteria criteria = orderCriteria.createCriteria();
        criteria.andOrderNoEqualTo(orderNo);
        criteria.andUserIdEqualTo(userId);
        Order order = orderMapper.selectByExample(orderCriteria).get(0);
        if (order == null) {
            return Result.error("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()) {
            return Result.success();
        }
        return Result.error();
    }

    @Override
    public void closeOrder(int hour) {
        Date closeDateTime = DateUtils.addHours(new Date(), -hour);
        //List<Order> orderList = orderMapper.selectOrderStatusByCreateTime(Const.OrderStatusEnum.NO_PAY.getCode(), DateTimeUtil.dateToStr(closeDateTime));
        List<Order> orderList = Lists.newArrayList();//当前时间之前的半小时状态为未支付的订单
        for (Order order : orderList) {
            //List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            List<OrderItem> orderItemList = Lists.newArrayList();//根据订单号，获取订单详情
            for (OrderItem orderItem : orderItemList) {

                //一定要用主键where条件，防止锁表。同时必须是支持MySQL的InnoDB。
                // Integer stock = productMapper.selectStockByProductId(orderItem.getProductId());
                Integer stock = 0;
                //考虑到已生成的订单里的商品，被删除的情况
                if (stock == null) {
                    continue;
                }
                ProductWithBLOBs product = new ProductWithBLOBs();
                product.setId(orderItem.getProductId());
                product.setStock(stock + orderItem.getQuantity());
                productMapper.updateByPrimaryKeySelective(product);
            }
            //关闭订单 状态 status = 0
//            orderMapper.closeOrderByOrderId(order.getId());
            logger.info("关闭订单OrderNo：{}", order.getOrderNo());
        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtils.addHours(new Date(), -1));
    }

}
