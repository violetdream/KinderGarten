package com.wechat.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

public class Demo {
    public static void main(String[] args) {
        //生成请求参数签名
        String token="88bd7e6ec57d613f2676b2e395c26dac";
        String timestamp=String.valueOf(System.currentTimeMillis());
        String nonce=WXBizMsgCrypt.getRandomStr();
        String encrypt="";
        String[] array = new String[] { token, timestamp, nonce, encrypt };
        System.out.println("array = " + Arrays.deepToString(array));
        try {
            String signature=SHA1.getSHA1(token,timestamp,nonce,encrypt);
            System.out.println("signature="+signature);
            System.out.println(URLDecoder.decode("https%3A%2F%2Flite3.sunlands.com%2Ffund-index%2F%3Fstate%3D&response_type=code&scope=snsapi_base&connect_redirect=1#wechat_redirect"));

        } catch (AesException e) {
            e.printStackTrace();
        }
    }
}
