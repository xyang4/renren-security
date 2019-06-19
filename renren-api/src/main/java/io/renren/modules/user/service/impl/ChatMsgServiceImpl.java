package io.renren.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.modules.user.dao.ChatMsgDao;
import io.renren.modules.user.entity.ChatMsgEntity;
import io.renren.modules.user.form.MsgForm;
import io.renren.modules.user.service.ChatMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;




@Service("chatMsgService")
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgDao, ChatMsgEntity> implements ChatMsgService {

    @Autowired
    private ChatMsgDao msgDao;

    @Override
    public R sendMsg(Integer userId, MsgForm msgForm) {
        ChatMsgEntity chatMsgEntity = new ChatMsgEntity();
        chatMsgEntity.setSendUserId(userId);
        chatMsgEntity.setRecvUserId(msgForm.getRecvUserId());
        chatMsgEntity.setMsgType(msgForm.getMsgType());
        chatMsgEntity.setCharText(msgForm.getCharText());
        chatMsgEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        msgDao.insert(chatMsgEntity);
        return R.ok();
    }

    @Override
    public List<ChatMsgEntity> getPageList(Integer userId, Integer pageIndex, Integer pageSize) {
        Page<ChatMsgEntity> page = new Page<>(pageIndex,pageSize);
        page.setRecords(msgDao.getPageList(page,userId));
        return page.getRecords();
    }
}
