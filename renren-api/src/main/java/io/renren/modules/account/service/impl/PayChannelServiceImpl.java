package io.renren.modules.account.service.impl;

import io.renren.modules.account.dao.PayChannelDao;
import io.renren.modules.account.entity.PayChannelEntity;
import io.renren.modules.account.form.PayChannelForm;
import io.renren.modules.account.service.PayChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;


@Service("payChannelService")
public class PayChannelServiceImpl extends ServiceImpl<PayChannelDao, PayChannelEntity> implements PayChannelService {

    @Autowired
    private PayChannelDao payChannelDao;

    @Override
    public int addPayChannel(PayChannelEntity payChannel) {
        return payChannelDao.insert(payChannel);
    }

    @Override
    public List<PayChannelEntity> getPayChannelListByUserId(Integer userId) {
        return payChannelDao.getPayChannelListByUserId(userId);
    }
}
