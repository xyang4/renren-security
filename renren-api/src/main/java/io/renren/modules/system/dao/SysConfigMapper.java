package io.renren.modules.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.system.entity.SysConfig;

public interface SysConfigMapper extends BaseMapper<SysConfig> {
    SysConfig selectConfigById(Long configId);
    String selectConfigByKey(String key);
}
