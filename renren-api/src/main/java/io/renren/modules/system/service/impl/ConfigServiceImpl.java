package io.renren.modules.system.service.impl;

import io.renren.modules.system.dao.SysConfigMapper;
import io.renren.modules.system.entity.SysConfig;
import io.renren.modules.system.service.IConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 参数配置 服务层实现
 *
 */
@Service
public class ConfigServiceImpl implements IConfigService {

    @Autowired
    private SysConfigMapper dao;

    @Override
    public SysConfig selectConfigById(Long configId) {
        return dao.selectConfigById(configId);
    }

    @Override
    public String selectConfigByKey(String configKey) {
        return dao.selectConfigByKey(configKey);
    }
}
