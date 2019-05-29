package io.renren.modules.system.service;

import io.renren.modules.system.entity.SysConfig;

/**
 * 参数配置 服务层
 *
 */
public interface IConfigService {

    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigByKey(String configKey);
}
