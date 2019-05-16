package io.renren.modules.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    RedisTemplate redisTemplate;
    // 符串/字符串类型数据
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    // 复杂的对象类型，不做任何转换的
    @Autowired
    ValueOperations<String, String> valueOperations;

    @Autowired
    HashOperations<String, String, String> hashOperations;
    @Autowired
    ListOperations<String, String> listOperations;
    @Autowired
    SetOperations<String, String> setOperations;
    @Autowired
    ZSetOperations<String, String> zSetOperations;

    @Override
    public String getHash(String H, String HK) {
        return null;
    }

    @Override
    public String getHashStrVal(String key, String hashKey) {
        HashOperations<String, String, String> ho = stringRedisTemplate.opsForHash();

        return ho.get(key, hashKey);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }


    @Override
    public Long delete(String hashKey, String hashKeys) {
        return stringRedisTemplate.opsForHash().delete(hashKey, hashKeys);
    }

    @Override
    public String getVal(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean delKey(String key) {
        return stringRedisTemplate.delete(key);
    }

    @Override
    public Long getExpire(String key, TimeUnit timeUnit) {
        return stringRedisTemplate.getExpire(key, timeUnit);
    }

    @Override
    public boolean putHashKey(String key, String field, String val) {
        hashOperations.put(key, field, val);
        return true;

    }

    @Override
    public <T> T hGet(String key, String field, Class<T> clazz) {
        String obj = hGet(key, field);
        if (null == obj) {
            return null;
        }
        return JSONObject.parseObject(obj, clazz);

    }

    @Override
    public String hGet(String key, String field) {
        return hashOperations.get(key, field);

    }

    @Override
    public void setAdd(String key, String val) {
        setOperations.add(key, val);
    }

    @Override
    public void putHashKeyWithObject(String key, String hKey, String hVal) {
        hashOperations.put(key, hKey, hVal);
    }

    @Override
    public void leftPush(String key, String val) {
        listOperations.leftPush(key, val);
    }

    @Override
    public String pull(String key) {
        return listOperations.leftPop(key);
    }

    @Override
    public void sendMessageToQueue(RedisMessageDomain message) {
        if (null == message || null == message.getTopic()) {
            log.warn(RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR.getMsg());
            return;
        }
      /*  String content;
        if (message.getContent() instanceof String) {
            content = (String) message.getContent();
        } else {
            content = JSONObject.toJSONString(message);
        }*/
        stringRedisTemplate.convertAndSend(message.getTopic().getCommand(), JSONObject.toJSONString(message));
    }

    @Override
    public boolean isSetMember(String key, String val) {

        return setOperations.isMember(key, val);
    }

    @Override
    public Long removeSetMember(String key, String val) {
        return setOperations.remove(key, val);
    }

    @Override
    public Set<String> setMembers(String key) {
        return setOperations.members(key);
    }


}
