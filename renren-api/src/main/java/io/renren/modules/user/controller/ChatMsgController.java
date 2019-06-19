package io.renren.modules.user.controller;


import io.renren.common.annotation.AppLogin;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.user.entity.ChatMsgEntity;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.form.MsgForm;
import io.renren.modules.user.service.ChatMsgService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.R;

import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-18 17:49:43
 */
@Slf4j
@RestController
@RequestMapping("app/chatmsg")
public class ChatMsgController extends BaseController {
    @Autowired
    private ChatMsgService chatMsgService;


    @AppLogin
    @ApiOperation("客服-发送消息")
    @RequestMapping("/send")
    public R sendMsg(@RequestBody MsgForm msgForm){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        return chatMsgService.sendMsg(tokenEntity.getUserId(),msgForm);
    }


    @AppLogin
    @ApiOperation("客服消息列表")
    @RequestMapping("/pageList")
    public R pageList(@RequestBody Map paramMap){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //默认第一页5条
        Integer pageIndex = paramMap.get("pageIndex")==null ? 1 : (Integer) paramMap.get("pageIndex");
        Integer pageSize =  paramMap.get("pageSize")==null ? 5 : (Integer) paramMap.get("pageSize");
        List<ChatMsgEntity> msgEntityList = chatMsgService.getPageList(tokenEntity.getUserId(),pageIndex,pageSize);
        return R.ok(msgEntityList);
    }

}
