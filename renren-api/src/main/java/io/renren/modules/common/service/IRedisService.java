package io.renren.modules.common.service;

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

    void putHashKeyWithObject(String h, String hKey, String hVal);

    void sendMessageToQueue(String queneName, Object message);

}
