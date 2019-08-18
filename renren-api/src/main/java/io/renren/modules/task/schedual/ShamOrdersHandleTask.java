package io.renren.modules.task.schedual;

import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.system.service.IConfigService;
import io.renren.modules.task.BaseHandleTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 发起假订单程序
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "renren", name = "task-open", havingValue = "true")
public class ShamOrdersHandleTask extends BaseHandleTask {

    @Autowired
    OrdersService ordersService;

    @Autowired
    private IConfigService configService;
    /**
     * 发起假订单程序
     */
    @Scheduled(fixedRate = 8 * 1000)
    public void shamOrders() {
        log.info("发起假订单程序开始。。。");
        //判断是否启用时间段
        String sham_orders = configService.selectConfigByKey("sham_orders");
        if(StringUtils.isNotEmpty(sham_orders)){
            String[] sham = sham_orders.split("-");
            Date date = new Date();
            //设置要获取到什么样的时间
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            //获取String类型的时间
            String createdate = sdf.format(date);
            if(createdate.compareTo(sham[0])<0){
                log.info("假订单程序暂停。时间未开始"+sham[0]);
                return;
            }
            if(createdate.compareTo(sham[1])>0){
                log.info("假订单程序暂停。时间已结束"+sham[1]);
                return;
            }
        }else {
            log.info("假订单程序暂停。返回。null");
            return;
        }
        //查询频次
        for(int i=0;i<1;i++){
            int ii=(int)(Math.random()*20+1);
            if(ii>10){
                log.info("假订单程序随机为不发单。。返回。");
                return;
            }

            Integer merId=900;
            String orderDate="";
            String payType="digicash";
            String sendAmount="100";
            ordersService.shamOrders( merId, orderDate, payType, sendAmount);
            try {
                TimeUnit.SECONDS.sleep(ii);
                //Thread.sleep(ii);
                System.out.println(new Date());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("发起假订单程序结束。。。");

    }


}
