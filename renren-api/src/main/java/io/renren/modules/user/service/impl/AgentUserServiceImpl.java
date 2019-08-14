/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.user.dao.AgentUserDao;
import io.renren.modules.user.entity.AgentUserEntity;
import io.renren.modules.user.service.AgentUserService;
import io.renren.modules.user.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 推荐人用户
 *
 */
@Service
public class AgentUserServiceImpl extends ServiceImpl<AgentUserDao, AgentUserEntity> implements AgentUserService {

    @Autowired
    AgentUserDao agentUserDao;
    //推荐人列表
    public List<Map<String, Object>> agentUserList(Map<String, Object> param){
        return agentUserDao.agentUserList(param);
    }

    //修改
    public int agentUserEdit(Map<String, Object> param){

        return agentUserDao.agentUserEdit(param);
    }

    /**
     * 代理商每日结算
     */
    public void agentSettle(){

        //查询所有代理上

        //查询收益

    }

}
