/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.user.entity.AgentUserEntity;

import java.util.List;
import java.util.Map;

/**
 * 推荐人用户
 *
 */
public interface AgentUserService extends IService<AgentUserEntity> {

    //推荐人列表
    List<Map<String, Object>> agentUserList(Map<String, Object> param);

    //修改
    int agentUserEdit(Map<String, Object> param);



}
