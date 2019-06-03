package io.renren.modules.account.service.impl;

import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.account.dao.PayChannelDao;
import io.renren.modules.account.entity.ImgEntity;
import io.renren.modules.account.entity.PayChannelDetail;
import io.renren.modules.account.entity.PayChannelEntity;
import io.renren.modules.account.form.UpdatePayChannelFrom;
import io.renren.modules.account.service.ImgService;
import io.renren.modules.account.service.PayChannelService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Service("payChannelService")
public class PayChannelServiceImpl extends ServiceImpl<PayChannelDao, PayChannelEntity> implements PayChannelService {

    @Autowired
    private PayChannelDao payChannelDao;
    @Autowired
    private ImgService imgService;

    @Override
    public int addPayChannel(PayChannelEntity payChannel) {
        return payChannelDao.insert(payChannel);
    }

    @Override
    public List<PayChannelEntity> getPayChannelListByUserId(Map<String, Object> param) {
        return payChannelDao.getPayChannelListByUserId(param);
    }

    @Override
    public List<Map<String, Object>> getPayChannelGroupData(Integer userId) {
        return payChannelDao.getPayChannelGroupData(userId);
    }

    @Override
    public R updatePayChannel(UpdatePayChannelFrom updatePayChannelFrom) {
        PayChannelEntity payChannelEntity = payChannelDao.selectById(updatePayChannelFrom.getPayChannelId());
        if(payChannelEntity == null){
            return R.error(-101,updatePayChannelFrom.getPayChannelId()+"支付类型不存在");
        }
        PayChannelEntity update = new PayChannelEntity();
        update.setPayChannelId(Integer.parseInt(updatePayChannelFrom.getPayChannelId()));
        update.setAccountName(updatePayChannelFrom.getAccountName());
        update.setAccountNo(updatePayChannelFrom.getAccountNo());

        //图片不为空并且和存在的图片不一样
        if(StringUtils.isNoneBlank(updatePayChannelFrom.getBaseImg()) ){
            //查询已存在的照片
            ImgEntity imgEntity = imgService.getById(payChannelEntity.getQrimgId());
            String existBaseImg = imgEntity == null?"":imgEntity.getBase64();
            if(!updatePayChannelFrom.getBaseImg().equals(existBaseImg)){
                //添加上传图片
                ImgEntity img = new ImgEntity();
                img.setBase64(updatePayChannelFrom.getBaseImg());
                img.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
                int r1 = imgService.addImg(img);
                if(r1 > 0){
                    update.setQrimgId(img.getImgId());
                }
            }

        }
        //使用状态
        if(StringUtils.isNoneBlank(updatePayChannelFrom.getUseStatus())){
            update.setUseStatus(Integer.parseInt(updatePayChannelFrom.getUseStatus()));
        }
        //绑定状态
        if(StringUtils.isNoneBlank(updatePayChannelFrom.getBindStatus())){
            update.setBindStatus(Integer.parseInt(updatePayChannelFrom.getBindStatus()));
        }
        payChannelDao.updateById(update);
        return R.ok();
    }

    @Override
    public PayChannelDetail getPayChannelDetailById(Integer payChannelId) {
        return payChannelDao.getPayChannelDetailById(payChannelId);
    }

    @Override
    public List<PayChannelDetail> getPayChannels(Map<String, Object> params) {
        return payChannelDao.getPayChannels(params);}
}
