package io.renren.modules.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.account.entity.PayChannelEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-26 13:48:36
 */
public interface PayChannelService extends IService<PayChannelEntity> {

    //添加支付方式
    int addPayChannel(PayChannelEntity payChannelForm);

    List<PayChannelEntity> getPayChannelListByUserId(Map<String, Object> param);

    List<Map<String, Object>> getPayChannelGroupData(Integer userId);
}

