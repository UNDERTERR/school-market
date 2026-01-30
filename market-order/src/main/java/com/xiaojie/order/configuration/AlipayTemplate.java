package com.xiaojie.order.configuration;


import com.xiaojie.order.vo.PayVo;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayTemplate {

    public String pay(PayVo  payVo){

//TODO alipay api

        return null;
    }

    public String getAlipay_public_key() {
        return "TODO";
    }

    public String getCharset() {
        return "UTF-8";
    }

    public String getSign_type() {
        return "RSA2";
    }
}
