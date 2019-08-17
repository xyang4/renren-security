package io.renren.modules.agent.controller;

import io.renren.common.utils.R;
import io.renren.modules.agent.service.AgentSettleDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 代理结算收益记录表
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:25
 */
@RestController
@RequestMapping("agent/agentsettledetail")
@Api(tags = "代理相关")
public class AgentSettleDetailController {
    @Autowired
    private AgentSettleDetailService agentSettleDetailService;

    @GetMapping
    @ApiOperation("执行代理费用结算统计")
    public R execReport(@RequestParam(value = "settleDate", required = false) String queryDate) {
        return R.ok(agentSettleDetailService.execProfitSettleReport(queryDate));
    }
}
