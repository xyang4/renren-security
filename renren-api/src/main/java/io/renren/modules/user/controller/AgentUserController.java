package io.renren.modules.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.system.service.IConfigService;
import io.renren.modules.user.dao.AgentUserDao;
import io.renren.modules.user.entity.AgentUserEntity;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.service.AgentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("推荐人用户相关")
@Slf4j
@RestController
@RequestMapping("app/agentUser")
public class AgentUserController extends BaseController {
    @Autowired
    AgentUserService agentUserService;
    @Autowired
    private IConfigService configService;
    @AppLogin
    @PostMapping("agentUserList")
    @ApiOperation("查询列表")
    public R agentUserList() {
        TokenEntity tokenEntity = getToken();
        if (null != (checkToken(tokenEntity))) {
            return R.error();
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("agentId",tokenEntity.getUserId());
        List<Map<String, Object>> list = agentUserService.agentUserList(param);
        return R.ok(list);
    }

    @AppLogin
    @PostMapping("agentUserEdit")
    @ApiOperation("修改")
    public R agentUserEdit(@RequestBody Map paramReq) {
        TokenEntity tokenEntity = getToken();
        if (null != (checkToken(tokenEntity))) {
            return R.error();
        }

        Integer agentUserId = (Integer) paramReq.get("agentUserId");
        BigDecimal recvChargeRate = new BigDecimal( (String)paramReq.get("recvChargeRate"));

        //查询用户是否是邀请用户，是邀请用户重新设置接单收益
        AgentUserEntity agentUserEntity= agentUserService.getOne(
                new QueryWrapper<AgentUserEntity>().eq("user_id", tokenEntity.getUserId()));
        if(agentUserEntity!=null){
            log.info("是邀请用户user_id {}",tokenEntity.getUserId());
            //查询自己的费率
            if(recvChargeRate.compareTo(agentUserEntity.getRecvChargeRate())>0){
                return R.error(-1,"无法修改，您最大可设置收益率为"+agentUserEntity.getRecvChargeRate());
            }
        }

        //查询公共推荐人最小收益费率
        String min = configService.selectConfigByKey("recv_mer_chargeRate");
        if(recvChargeRate.compareTo(new BigDecimal(min))<0){
            return R.error(-1,"费率低于最小收益率:"+min+"，无法修改");
        }

        //查询公共推荐人最大收益费率：商户充值接单奖励手续费-最大值
        String max = configService.selectConfigByKey("recv_mer_chargeRate_max");
        if(recvChargeRate.compareTo(new BigDecimal(max))>=0){
            return R.error(-1,"费率高于最大收益率:"+max+"，您将不能获得推荐收益");
        }

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("agentId",tokenEntity.getUserId());
        param.put("agentUserId",agentUserId);
        param.put("recvChargeRate",recvChargeRate);
        int num = agentUserService.agentUserEdit(param);
        if(num>0){
            return R.ok();
        }
        return R.error(-1,"更新失败");
    }


}
