package io.renren.modules.netty;

import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.R;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.netty.domain.RedisMessageDomain;
import io.renren.modules.netty.enums.WebSocketActionTypeEnum;
import io.renren.modules.netty.service.INettyService;
import io.renren.modules.user.entity.TokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("ws")
@AppLogin
public class WebSocketController extends BaseController {

    @Autowired
    INettyService iNettyService;

    @PostMapping
    public R send(@RequestBody Map<String, Object> vo) {
        TokenEntity tokenEntity = getToken();
        R r;
        if (null != (r = checkToken(tokenEntity))) {
            return r;
        }
        RedisMessageDomain messageDomain = new RedisMessageDomain(WebSocketActionTypeEnum.BEGIN_RECEIPT.getCommand(), tokenEntity.getMobile(), vo);
        return iNettyService.sendMessage(messageDomain, (Boolean) vo.get("async"));
    }
}
