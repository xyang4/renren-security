package io.renren.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.user.entity.ChatMsgEntity;
import io.renren.modules.user.form.MsgForm;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-18 17:49:43
 */
public interface ChatMsgService extends IService<ChatMsgEntity> {

    R sendMsg(Integer userId, MsgForm msgForm);

    List<ChatMsgEntity> getPageList(Integer userId, Integer pageIndex, Integer pageSize);
}

