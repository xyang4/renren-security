package io.renren.common.listener;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.utils.R;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * redis 消息队列监听器，//TODO 根据业务指定不同的消费者
 */
@Slf4j
@Component
public class RedisMessageReceiver implements MessageListener {
    @Autowired
    IRedisService iRedisService;
    @Autowired
    INettyService iNettyService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(pattern);
        String m = message.toString();
        log.info("Redis 消息队列消费开始：Message[{}].", m);
        if (!m.startsWith("{") || !m.endsWith("}")) {
            log.warn("消息格式错误");
            return;
        }

        RedisMessageDomain messageDomain = JSONObject.parseObject(m, RedisMessageDomain.class);
        if (null == messageDomain || null == messageDomain.getTopic() || null == messageDomain.getContent()) {
            log.warn("Redis 消息队列消费完成!! {}", RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR.getMsg());
            return;
        }

        R r = null;
        WebSocketActionTypeEnum actionTypeEnum = WebSocketActionTypeEnum.getByCode(topic);
        switch (actionTypeEnum) {

            default:
                log.warn("UnSupport Topic[ {} ].", topic);
        }
        log.info("Consumer Execution Done：{}.", r);
    }

}
