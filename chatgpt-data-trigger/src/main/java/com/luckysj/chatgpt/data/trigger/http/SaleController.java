package com.luckysj.chatgpt.data.trigger.http;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.google.common.eventbus.EventBus;
import com.luckysj.chatgpt.data.domain.auth.service.IAuthService;
import com.luckysj.chatgpt.data.domain.order.config.AliPayConfig;
import com.luckysj.chatgpt.data.domain.order.model.aggregates.CreateOrderAggregate;
import com.luckysj.chatgpt.data.domain.order.model.entity.PayOrderEntity;
import com.luckysj.chatgpt.data.domain.order.model.entity.ProductEntity;
import com.luckysj.chatgpt.data.domain.order.model.entity.ShopCartEntity;
import com.luckysj.chatgpt.data.domain.order.model.valobj.AliPayTradeTypeVo;
import com.luckysj.chatgpt.data.domain.order.service.IOrderService;
import com.luckysj.chatgpt.data.trigger.http.dto.SaleProductDTO;
import com.luckysj.chatgpt.data.types.common.Constants;
import com.luckysj.chatgpt.data.types.exception.ChatGPTException;
import com.luckysj.chatgpt.data.types.model.Response;
import com.luckysj.chatgpt.data.types.util.QRCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author www.luckysj.top 刘仕杰
 * @description 销售业务服务接口
 * @create 2023/12/10 17:23:33
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/sale/")
public class SaleController {
    @Resource
    private IAuthService authService;
    @Resource
    private IOrderService orderService;
    @Resource
    private AliPayConfig aliPayConfig;
    @Resource
    private AlipayClient alipayClient;
    // @Resource
    // private EventBus eventBus; //事件总线，用来发布消息
    @Resource(name = "delivery")
    private RTopic redisTopic;

    /**
    * @description 查询商品列表
    * @param token 用户token
    * @return
    * @date 2023/12/15 10:26:14
    */
    @RequestMapping(value = "query_product_list", method = RequestMethod.GET)
    public Response<List<SaleProductDTO>> queryProductList(@RequestHeader("Authorization") String token) {
        try {
            // 1. Token 校验
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<List<SaleProductDTO>>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }
            // 2. 查询商品
            List<ProductEntity> productEntityList = orderService.queryProductList();
            log.info("商品查询 {}", JSON.toJSONString(productEntityList));

            List<SaleProductDTO> mallProductDTOS = new ArrayList<>();
            for (ProductEntity productEntity : productEntityList) {
                SaleProductDTO mallProductDTO = SaleProductDTO.builder()
                        .productId(productEntity.getProductId())
                        .productName(productEntity.getProductName())
                        .productDesc(productEntity.getProductDesc())
                        .price(productEntity.getPrice())
                        .quota(productEntity.getQuota())
                        .build();
                mallProductDTOS.add(mallProductDTO);
            }

            // 3. 返回结果
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(mallProductDTOS)
                    .build();
        } catch (Exception e) {
            log.error("商品查询失败", e);
            return Response.<List<SaleProductDTO>>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
    * @description 用户商品下单
    * @param productId 商品id
    * @param token 用户token
    * @date 2023/12/15 10:23:56
    */
    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    public Response<String> createParOrder(@RequestHeader("Authorization") String token, @RequestParam Integer productId) {
        try {
            // 1. Token 校验
            boolean success = authService.checkToken(token);
            if (!success) {
                return Response.<String>builder()
                        .code(Constants.ResponseCode.TOKEN_ERROR.getCode())
                        .info(Constants.ResponseCode.TOKEN_ERROR.getInfo())
                        .build();
            }

            // 2. Token 解析
            String openid = authService.parseOpenid(token);
            assert null != openid;
            log.info("用户商品下单，根据商品ID创建支付单开始 openid:{} productId:{}", openid, productId);

            // 封装参数
            ShopCartEntity shopCartEntity = ShopCartEntity.builder()
                    .openid(openid)
                    .productId(productId).build();

            PayOrderEntity payOrder = orderService.createOrder(shopCartEntity);
            log.info("用户商品下单，根据商品ID创建支付单完成 openid: {} productId: {} orderPay: {}", openid, productId, payOrder.toString());

            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrder.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("用户商品下单，根据商品ID创建支付单失败", e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
    * @description 扫描支付二维码转接接口，该接口会请求支付宝，将支付界面完整的表单html输出到页面
    * @param payNo 订单号(这个参数需要和qrcode_url里面的参数相对应，否则接受不到)
    * @return
    * @date 2023/12/15 10:22:32
    */
    @GetMapping("/requestpay")
    public void requestpay(String payNo,HttpServletResponse httpResponse) throws IOException {
        log.info("用户支付，请求支付宝下单接口，orderId：{}", payNo);

        // 根据订单号查询到定义订单的相关消息
        CreateOrderAggregate createOrderAggregate = orderService.queryOrder(payNo);

        BigDecimal totalAmount = createOrderAggregate.getOrder().getTotalAmount(); //金额
        //创建API对应的request
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        //设置通知地址，当支付成功后支付宝官网会发起Post请求到该链接，luckysj.natapp1.cc为你的内网映射地址或者服务器域名
        alipayRequest.setNotifyUrl(aliPayConfig.getNOTIFY_URL());
        // 设置BizContent，存放交易的信息内容
        // out_trade_no 商户订单号
        // total_amount 订单金额
        // subject 订单标题（显示给用户）
        // product_code 销售产品码，网站支付填写固定值QUICK_WAP_WAY
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\""+payNo+"\"," +
                " \"total_amount\":\""+totalAmount+"\"," +
                " \"subject\":\""+ "对话额度"+"\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");//填充业务参数
        String form = "";
        try {
            //请求支付宝下单接口,发起http请求
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + aliPayConfig.CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }

    /**
    * @description 接受支付结果通知的接口
    * @date 2023/12/15 11:43:54
    */
    @PostMapping("/paynotify")
    public void paynotify(HttpServletRequest request,HttpServletResponse response) throws Exception {
        // 获取请求中的参数，并将其存储在params中
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        try {
            log.info("开始处理支付宝支付通知");

            // 验签
            boolean verify_result = AlipaySignature.rsaCheckV1(params, aliPayConfig.getALIPAY_PUBLIC_KEY(),
                    aliPayConfig.CHARSET, "RSA2");

            if(verify_result) {
                // 验签成功
                // 商户订单号
                String orderId = new String(params.get("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
                // 支付宝交易号
                String out_trade_no = new String(params.get("trade_no").getBytes("ISO-8859-1"),"UTF-8");
                // 交易状态
                String trade_status = new String(params.get("trade_status").getBytes("ISO-8859-1"),"UTF-8");
                // 交易总金额
                String total_amount_str = new String(params.get("total_amount").getBytes("ISO-8859-1"),"UTF-8");
                BigDecimal total_amount = new BigDecimal(total_amount_str);
                // 交易时间
                String pay_time_str = new String(params.get("gmt_create").getBytes("ISO-8859-1"),"UTF-8");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date pay_time = sdf.parse(pay_time_str);

                //默认情况只有交易成功TRADE_SUCCESS的情况会触发通知，其他情况默认不通知
                if (AliPayTradeTypeVo.TRADE_SUCCESS.getCode().equals(trade_status)) {
                    // 支付成功后的业务逻辑
                    log.info("用户支付成功，订单号：{}，支付宝交易号：{}", orderId, out_trade_no);

                    // 更新订单信息
                    boolean isSuccess = orderService.changeOrderPaySuccess(orderId, out_trade_no, total_amount, pay_time);
                    // 发布发货消息
                    if(isSuccess){
                        // eventBus.post(orderId);
                        redisTopic.publish(orderId);
                    }
                } else if (AliPayTradeTypeVo.TRADE_FINISHED.getCode().equals(trade_status)) {
                    // 交易结束，不可退款。
                } else if (AliPayTradeTypeVo.TRADE_CLOSED.getCode().equals(trade_status)){
                    // 未付款交易超时关闭，或支付完成后全额退款。
                } else if (AliPayTradeTypeVo.WAIT_BUYER_PAY.getCode().equals(trade_status)){
                    // 交易创建，等待买家付款。
                }
                response.getWriter().write("success");
            }else{
                // 通常，在处理支付宝支付结果通知时，如果验签成功并且业务处理成功，应该返回"success"，告诉支付宝服务器通知已经成功接收并处理。
                // 如果验签失败或者业务处理失败，应该返回"fail"，告诉支付宝服务器通知处理失败，支付宝服务器会继续发送通知，直到收到"success"为止。
                response.getWriter().write("fail");
            }
        } catch (Exception e) {
            log.error("处理支付通知结果时出现错误，错误信息:{}", e.getMessage());
            e.printStackTrace();
            response.getWriter().write("fail");
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }
        // String result = orderService.receiveNotify(params);
    }
}
