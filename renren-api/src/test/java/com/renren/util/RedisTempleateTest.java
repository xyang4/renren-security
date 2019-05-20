package com.renren.util;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RedisTempleateTest {
    private static RedisTemplate<String, Object> redisTemplate;
    private static SetOperations setOperations;
    private static HashOperations<String, String, Object> hashOperations;
    private static GeoOperations<String, Object> geoOperations;
    private static ListOperations<String, Object> listOperations;
    private static ValueOperations<String, Object> valueOperations;
    private static HyperLogLogOperations<String, Object> hyperLogLogOperations;
    private static ClusterOperations<String, Object> clusterOperations;

    @Before
    public void init() {

        redisTemplate = new RedisTemplate<>();

        // 1 链接工厂配置
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(9);
        configuration.setPassword("123456");
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(configuration);
        redisTemplate.setConnectionFactory(connectionFactory);
        // 2 序列化机制配置
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        // 3 操作类获取
        setOperations = redisTemplate.opsForSet();
        geoOperations = redisTemplate.opsForGeo();
        hashOperations = redisTemplate.opsForHash();
        listOperations = redisTemplate.opsForList();
        clusterOperations = redisTemplate.opsForCluster();
        hyperLogLogOperations = redisTemplate.opsForHyperLogLog();
        valueOperations = redisTemplate.opsForValue();
    }

    private static int INIT_CAPACITY = 10;

    @Test
    public void valTest() {

        List<String> keyList = new ArrayList<>(INIT_CAPACITY);
        for (int i = 0; i < 10; i++) {
            keyList.add("K_" + i);
            valueOperations.set("K_" + i, LocalDateTime.now().toString());
        }
        // print
        valueOperations.multiGet(keyList).forEach(System.out::println);
    }

    @Test
    public void hashSet() {
        String hK = "H:ch";
        List<String> keyList = new ArrayList<>(INIT_CAPACITY);
        for (int i = 0; i < INIT_CAPACITY; i++) {
            keyList.add("K_" + i);
            hashOperations.put(hK, "K_" + i, new Date());
            // Object must imple serializer interface or exception catch with: java.lang.IllegalArgumentException: DefaultSerializer requires a Serializable payload but received an object of type [com.renren.util.RedisTempleateTest$1]
            /*
             hashOperations.put(hK, "K_" + i, new Channel() {
                    @Override
                    public boolean isOpen() {
                        return false;
                    }

                    @Override
                    public void close() throws IOException {

                    }
                });
            }
            */
            // print
            hashOperations.multiGet(hK, keyList).forEach(System.out::println);
        }
    }

    /**
     * redis 序列化测试
     * <p>
     * 1.default:JdkSerializationRedisSerializer
     * 2.StringRedisSerializer
     * 3.GenericJackson2JsonRedisSerializer
     */
    @Test
    public void serialTest() {
        String hK = "H:serial";
        Object val = LocalDateTime.now();
        hashOperations.put(hK, "default", val);

        setHashValueSerializerAndFlush(new JdkSerializationRedisSerializer());
        // use default
        hashOperations.put(hK, "JdkSerializationRedisSerializer", val);

        // use Jackson2Json 提供 bean <--> json 间的相互装换 jackson工具在序列化和反序列化时，需要明确指定Class类型
        setHashValueSerializerAndFlush(new Jackson2JsonRedisSerializer<>(Object.class));
        hashOperations.put(hK, "Jackson2JsonRedisSerializer", val);

        // use FastJson
        setHashValueSerializerAndFlush(new FastJsonRedisSerializer<>(Object.class));
        hashOperations.put(hK, "FastJsonRedisSerializer", val);

        // use generic
        setHashValueSerializerAndFlush(new GenericFastJsonRedisSerializer());
        hashOperations.put(hK, "GenericFastJsonRedisSerializer", val);

        // use
        setHashValueSerializerAndFlush(new GenericJackson2JsonRedisSerializer());
        hashOperations.put(hK, "GenericJackson2JsonRedisSerializer", val);

        setHashValueSerializerAndFlush(new StringRedisSerializer());
        // java.lang.ClassCastException: java.time.LocalDateTime cannot be cast to java.lang.String
        hashOperations.put(hK, "StringRedisSerializer", val.toString());
    }

    private <T> void setHashValueSerializerAndFlush(RedisSerializer<T> redisSerializer) {
        redisTemplate.setHashValueSerializer(redisSerializer);
        redisTemplate.afterPropertiesSet();
    }

    /**
     * redis 事务测试： 一行多个命令，直到 exec 前
     * 用前需要先开启事务①
     */
    @Test
    public void transactionTest() {
        // 开启事务
        String hK = "H:tran";
        hashOperations.put(hK, "begin", "begin transaction!!!");
        redisTemplate.setEnableTransactionSupport(true);  // ①
        redisTemplate.multi();
        hashOperations.put(hK, "time", LocalDateTime.now());
        redisTemplate.exec();
        hashOperations.delete(hK, "begin");
    }

    @Test
    public void keysTest() {
        redisTemplate.keys("K_*").forEach(System.out::println);
    }

    @Test
    public void pipTest() {
        String key = "pip:";
        redisTemplate.executePipelined((RedisConnection connection) -> {
            connection.openPipeline();
            for (int i = 0; i < 10; i++) {
//                connection.zCount((key + i).getBytes(), 0, Integer.MAX_VALUE);
                connection.set((key + i).getBytes(), (i + "").getBytes());
            }
//            connection.closePipeline();
            return null;
        }, redisTemplate.getStringSerializer());
    }
}
