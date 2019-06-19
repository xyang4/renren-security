package io.renren.modules.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.user.entity.ChatMsgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-18 17:49:43
 */
@Mapper
public interface ChatMsgDao extends BaseMapper<ChatMsgEntity> {

    List<ChatMsgEntity> getPageList(Page<ChatMsgEntity> page, @Param("userId")Integer userId);
}
