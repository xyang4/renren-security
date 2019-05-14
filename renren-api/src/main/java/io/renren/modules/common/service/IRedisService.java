package io.renren.modules.common.service;

import io.renren.modules.netty.domain.RedisMessageDomain;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IRedisService {
    String getHash(String H, String HK);

    String getHashStrVal(String key, String hashKey);

    void set(String key, String value, long timeout, TimeUnit timeUnit);

    Long delete(String hashKey, String hashKeys);

    String getVal(String key);

    Long getExpire(String key, TimeUnit timeUnit);

    boolean delKey(String key);

    boolean putHashKey(String key, String field, String value);

    <T> T hGet(String key, String field, Class<T> clazz);

    String hGet(String key, String field);

    void setAdd(String key, String val);

    void putHashKeyWithObject(String h, String hKey, String hVal);


    void leftPush(String key, String val);

    String pull(String key);

    /**
     * 发送消息至指定队列
     *
     * @param message
     */
    void sendMessageToQueue(RedisMessageDomain message);

    boolean isSetMember(String key, String o);

    Long removeSetMember(String key, String val);

    /**
     * 获取集合中的所有元素
     */
    Set<String> setMembers(String key);

}
