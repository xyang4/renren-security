package io.renren.modules.account.controller;

import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.account.entity.ImgEntity;
import io.renren.modules.account.entity.PayChannelEntity;
import io.renren.modules.account.form.PayChannelForm;
import io.renren.modules.account.service.ImgService;
import io.renren.modules.account.service.PayChannelService;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/payChannel")
@Api("支付渠道相关")
public class PayChannelController extends BaseController {

    @Autowired
    private PayChannelService payChannelService;
    @Autowired
    private ImgService imgService;

    /**
     * 添加支付类型
     * @param payChannelForm
     * @return
     */
    @ApiOperation("添加支付方式")
    @RequestMapping("/addPayChannel")
    public R addPayChannel(@RequestBody PayChannelForm payChannelForm){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error();
        }
        PayChannelEntity payChannelEntity = new PayChannelEntity();
        payChannelEntity.setUserId(tokenEntity.getUserId());
        payChannelEntity.setAccountName(payChannelForm.getAccountName());
        payChannelEntity.setAccountNo(payChannelForm.getAccountNo());
        payChannelEntity.setAccountUid(payChannelForm.getAccountUid());
        payChannelEntity.setBankName(payChannelForm.getBankName());
        payChannelEntity.setPayType(payChannelForm.getPayType());
        //判断是否上传图片
        if(StringUtils.isNoneBlank(payChannelForm.getBaseImg())){
            //添加上传图片
            ImgEntity imgEntity = new ImgEntity();
            imgEntity.setBase64(payChannelForm.getBaseImg());
            imgEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            int r1 = imgService.addImg(imgEntity);
            if(r1 > 0){
                payChannelEntity.setQrimgId(imgEntity.getImgId());
            }
        }
        payChannelService.addPayChannel(payChannelEntity);
        return R.ok();
    }

    /**
     * 更新支付类型状态
     * @param payChannelId
     * @param useStatus
     * @param bindStatus
     * @return
     */
    @ApiOperation("更新支付方式")
    @RequestMapping("/updatePayChannel")
    public R updatePayChannel(@RequestParam(required = true,value = "payChannelId") String payChannelId,String useStatus,String bindStatus){
        PayChannelEntity payChannelEntity = payChannelService.getById(payChannelId);
        if(payChannelEntity == null){
            return R.error(-1,payChannelId+"支付渠道不存在");
        }
        PayChannelEntity update = new PayChannelEntity();
        update.setUserId(Integer.parseInt(payChannelId));
        if(StringUtils.isNoneBlank(useStatus)){
            update.setUseStatus(Integer.parseInt(useStatus));
        }
        if(StringUtils.isNoneBlank(bindStatus)){
            update.setBindStatus(Integer.parseInt(bindStatus));
        }
        payChannelService.updateById(update);
        return R.ok("操作成功");
    }


    @ApiOperation("查询支付方式")
    @RequestMapping("/list")
    public R payChannelFormList(String payType){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer userId = tokenEntity.getUserId();
        Map<String,Object> params =new HashMap<>();
        params.put("userId",userId);
        params.put("payType",payType);
        List<PayChannelEntity> payChannelEntityList = payChannelService.getPayChannelListByUserId(params);
        return R.ok(payChannelEntityList);
    }
}
