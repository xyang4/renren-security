package io.renren.modules.account.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.account.entity.ImgEntity;
import io.renren.modules.account.entity.PayChannelDetail;
import io.renren.modules.account.entity.PayChannelEntity;
import io.renren.modules.account.form.PayChannelForm;
import io.renren.modules.account.form.UpdatePayChannelFrom;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("app/payChannel")
@Api("支付渠道相关")
@AppLogin
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
    @AppLogin
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
     * 更新支付通道
     * @param updatePayChannelFrom
     * @return
     */
    @AppLogin
    @ApiOperation("更新支付内容")
    @RequestMapping("/updatePayChannel")
    public R updatePayChannel(@RequestBody UpdatePayChannelFrom updatePayChannelFrom){
        if(updatePayChannelFrom == null || StringUtils.isBlank(updatePayChannelFrom.getPayChannelId())){
            return R.error(-1,"请求参数错误");
        }
        return payChannelService.updatePayChannel(updatePayChannelFrom);
    }

    @AppLogin
    @ApiOperation("查询支付方式")
    @RequestMapping("/list")
    public R payChannelFormList(@RequestBody Map map){
        String payType = (String)map.get("payType");
        String useStatus = (String)map.get("useStatus");
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer userId = tokenEntity.getUserId();
        Map<String,Object> params =new HashMap<>();
        params.put("userId",userId);
        params.put("payType",payType);
        params.put("useStatus",useStatus);

        List<PayChannelEntity> payChannelEntityList = payChannelService.getPayChannelListByUserId(params);
        return R.ok(payChannelEntityList);
    }

    @AppLogin
    @ApiOperation("支付方式详情")
    @RequestMapping("/detail")
    public R detail(@RequestBody Map map){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Integer payChannelId = Integer.parseInt((String)map.get("payChannelId"));
        if(payChannelId == null){
            return R.error(-1001,"请求参数错误");
        }
        PayChannelDetail payChannelDetail = payChannelService.getPayChannelDetailById(payChannelId);
        return R.ok(payChannelDetail);
    }
}
