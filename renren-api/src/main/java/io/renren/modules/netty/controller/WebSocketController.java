package io.renren.modules.netty.controller;

import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ws")
@Api(tags = "WebSocket相关")
public class WebSocketController extends BaseController {

    @Autowired
    INettyService iNettyService;

    @ApiOperation("推送消息")
    @PostMapping("{type}")
    public R send(@ApiParam(allowableValues = "sync,async") @PathVariable("type") String handleType, @RequestBody RedisMessageDomain vo) {
        TokenEntity tokenEntity = getToken();
        R r;
        if (null != (r = checkToken(tokenEntity))) {
            return r;
        }
        RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.BEGIN_RECEIPT, System.currentTimeMillis(), vo.getContent());
        return iNettyService.sendMessage(messageDomain, "async".equals(handleType));
    }

    @GetMapping("online")
    @ApiOperation("在线用户查询")
    public R listOnlineUser() {

        return R.ok(iNettyService.listOnlineUser());
    }
}
