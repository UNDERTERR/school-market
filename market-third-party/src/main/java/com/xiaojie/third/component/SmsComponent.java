package com.xiaojie.third.component;

import com.xiaojie.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class SmsComponent {

    private String host = "https://gyytz.market.alicloudapi.com";
    private String path = "/sms/smsSend";
    private String appcode = "d680e3b7ea394379836c41fbbac4e467";

    // 从控制台获取这两个值
    private String templateId = "908e94ccf08b4476ba6c876d13f084ad";
    private String smsSignId = "2e65b1bb3d054466b82f0c9d125465e2";

    public boolean sendCode(String phone, String code) {
        String method = "POST";

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);  // 简单！

        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", phone);
        querys.put("templateId", templateId);    // 注意大小写
        querys.put("smsSignId", smsSignId);      // 必须有！
        querys.put("param", "**code**:" + code + ",**minute**:5");

        Map<String, String> bodys = new HashMap<>();

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("响应: " + result);
            return response.getStatusLine().getStatusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}