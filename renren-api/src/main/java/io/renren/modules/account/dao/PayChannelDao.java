package io.renren.modules.account.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.account.entity.PayChannelEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-26 13:48:36
 */
@Mapper
public interface PayChannelDao extends BaseMapper<PayChannelEntity> {

    List<PayChannelEntity> getPayChannelListByUserId(Map<String, Object> param);

    List<Map<String, Object>> getPayChannelGroupData(Integer userId);
}
