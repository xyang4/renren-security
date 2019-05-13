package io.renren.common.listener;

import com.alibaba.fastjson.JSONObject;
import io.renren.common.enums.RRExceptionEnum;
import io.renren.common.utils.R;
import io.renren.modules.common.service.IRedisService;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
        log.info("Consumer Execution Begin：Topic[{}] Message[{}].", topic, message.toString());

        RedisMessageDomain messageDomain = JSONObject.parseObject(message.toString(), RedisMessageDomain.class);
        if (null == messageDomain || StringUtils.isAnyBlank(messageDomain.getMobile())) {
            log.warn("Consumer Execution Done!! {}", RRExceptionEnum.MUST_PARAMS_DEFECT_ERROR.getMsg());
            return;
        }

        WebSocketActionTypeEnum actionTypeEnum = WebSocketActionTypeEnum.getByCode(topic);
        switch (actionTypeEnum) {
            case BEGIN_RECEIPT:
            case PRINT_SERVER_TIME:
                // 直接消费
                R r = iNettyService.sendMessage(messageDomain, false);
                log.info("Consumer Execution Done：{}.", r);
                break;
            default:
                log.warn("UnSupport Topic[ {} ].", topic);
                return;
        }
    }
}
