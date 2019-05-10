package io.renren.common.config;

import io.renren.common.listener.RedisMessageReceiver;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * redis 消息队列配置 // TODO 多队列配置
 */
@Configuration
@AutoConfigureAfter({RedisMessageReceiver.class})
public class RedisMessageSubscriberConfig {

    private static List<String> topicList = WebSocketActionTypeEnum.getCommands();

    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver);
    }

    /**
     * 创建消息监听容器
     *
     * @param redisConnectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(listenerAdapter, topicList.stream().map(v -> new PatternTopic(v)).collect(Collectors.toList()));
        return redisMessageListenerContainer;
    }
}
