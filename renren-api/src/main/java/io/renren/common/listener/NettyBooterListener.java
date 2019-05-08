package io.renren.common.listener;

import io.renren.common.config.RenrenProperties;
import io.renren.modules.websocket.service.INettyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyBooterListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    INettyService iNettyService;
    @Autowired
    RenrenProperties renrenProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            try {
                iNettyService.start(renrenProperties.getNettyPort());
            } catch (Exception e) {
                log.error("Netty Server Start Error:", e);
            }
        }
    }

}
