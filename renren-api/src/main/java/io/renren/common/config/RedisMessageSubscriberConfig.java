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
 * <a href="https://spring.io/guides/gs/messaging-redis/>官网Demo</a>
 */
@Configuration
@AutoConfigureAfter({RedisMessageReceiver.class})
public class RedisMessageSubscriberConfig {

    private static List<String> topicList = WebSocketActionTypeEnum.getCommands();

    /**
     * @param receiver
     * @return
     * @describe 注册统一的消费者
     */
    @Bean
    public MessageListenerAdapter listenerAdapter(RedisMessageReceiver receiver) {
        return new MessageListenerAdapter(receiver);
    }

    /**
     * @param redisConnectionFactory
     * @param listenerAdapter
     * @return
     * @describe 注册消息监听器，使用发布订阅者模式
     */
    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        // 便于简化处理，所有主题交由 RedisMessageReceiver 统一处理
        redisMessageListenerContainer.addMessageListener(listenerAdapter, topicList.stream().map(v -> new PatternTopic(v)).collect(Collectors.toList()));
        return redisMessageListenerContainer;
    }
}
