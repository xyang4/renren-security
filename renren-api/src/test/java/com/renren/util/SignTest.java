package com.renren.util;

import io.renren.common.utils.MD5Util;
import io.renren.common.utils.SignUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SignTest {
    @Test
    public void signCreateTest() {
        Map<String, Object> m = new HashMap<>();
        m.put("mobile", "18610450436");
        m.put("type", 0);
//        m.put("sign", "d6b77f2623b51f42cb7b5cf4cd818cd5");
        String signatureString = SignUtil.createSignatureString(m);
        String encrypt = MD5Util.encrypt(signatureString, "Hello World!");
        System.out.println(signatureString + "\n" + encrypt);
    }
}