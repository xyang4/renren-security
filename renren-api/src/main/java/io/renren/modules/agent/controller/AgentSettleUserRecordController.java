package io.renren.modules.agent.controller;

import io.renren.common.utils.R;
import io.renren.modules.agent.service.AgentSettleUserRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 代理每日收益记录
 *
 * @author xyang
 * @email 18610450436@163.com
 * @date 2019-08-17 14:10:24
 */
@RestController
@RequestMapping("agent/agentsettleuserrecord")
@Api(tags = "接单报表相关")
public class AgentSettleUserRecordController {
    @Autowired
    private AgentSettleUserRecordService agentSettleUserRecordService;

    @GetMapping
    @ApiOperation("执行用户接单统计")
    public R execReport(@RequestParam(value = "settleDate", required = false) String queryDate) {
        return R.ok(agentSettleUserRecordService.execUserRecvReport(queryDate));
    }

}
