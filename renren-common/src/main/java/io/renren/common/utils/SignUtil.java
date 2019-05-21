package io.renren.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * 签名工具类
 */
@UtilityClass
public class SignUtil {
    /**
     * 获取待签名字符串：1 筛选并排序 2 进行拼接
     *
     * @param domain
     * @return
     * @throws IllegalAccessException
     */
    public String createSignatureString(Object domain) {

        Map<String, Object> tMap = new HashMap<>();
        if (domain instanceof Map) {
            tMap = (Map<String, Object>) domain;
        } else {
            for (Field field : domain.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    tMap.put(field.getName(), field.get(domain));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        Map<String, Object> result = new TreeMap<>();

        tMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(x -> result.put(x.getKey(), x.getValue()));
        StringBuffer sb = null;
        int i = 1;
        for (Map.Entry item : result.entrySet()) {
            if (null == sb) {
                sb = new StringBuffer(result.size());
            }
            if (StringUtils.isEmpty(String.valueOf(item.getValue())) || item.getKey().equals(Constant.DATA_SIGN_KEY)) {
                i++;
                continue;
            }

            if (i < result.size()) {
                sb.append(item.getKey() + "=" + item.getValue() + "&");
            } else {
                sb.append(item.getKey() + "=" + item.getValue());
            }
            i++;
        }
        return sb.toString();
    }
}