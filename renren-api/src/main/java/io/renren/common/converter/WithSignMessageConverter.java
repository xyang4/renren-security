package io.renren.common.converter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import io.renren.common.config.RenrenProperties;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.exception.RRException;
import io.renren.common.utils.Constant;
import io.renren.common.utils.MD5Util;
import io.renren.common.utils.SignUtil;
import io.renren.common.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * FastJsonHttpMessageConverter 消息转换器扩展：
 * 1 添加数据签名校验
 * 2 其他逻辑处理...
 */
@Slf4j
public class WithSignMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws HttpMessageNotReadableException, IOException {
        Object r = checkDataSign(type, contextClass, inputMessage);
        // 其他逻辑处理
        return r;
    }

    /**
     * 数据签名校验
     *
     * @param type
     * @param contextClass
     * @param inputMessage
     * @return
     * @throws IOException
     */
    public Object checkDataSign(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException {

        RenrenProperties renrenProperties = SpringContextUtils.getBean(RenrenProperties.class);
        // 1 签名校验
        if (renrenProperties.isSignOpen()) {
            Map<String, Object> tempMap = JSON.parseObject(inputMessage.getBody(), super.getFastJsonConfig().getCharset(), Map.class, super.getFastJsonConfig().getFeatures());
            String originSignStr = (String) tempMap.get(Constant.DATA_SIGN_KEY);
            if (null == originSignStr) {
                throw new RRException(RRExceptionEnum.BAD_REQUEST_PARAMS, "缺少数据签名");
            }
            String newSigningStr = SignUtil.createSignatureString(tempMap);
            String newSignStr = MD5Util.encrypt(newSigningStr, renrenProperties.getDataSignKey());
            if (!newSignStr.equals(originSignStr)) {
                log.info("签名校验异常 False [{}] Correct[{}].", originSignStr, newSignStr);
                throw new RRException(RRExceptionEnum.BAD_REQUEST_PARAMS, "数据签名错误");
            }
            // 2 原样返回数据
            String r = JSON.toJSONString(tempMap);
            return JSON.parseObject(r, type);

        } else {
            return super.read(type, contextClass, inputMessage);
        }
    }
}