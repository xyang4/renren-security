package io.renren.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.user.dao.AgentSettleDao;
import io.renren.modules.user.dao.AgentUserDao;
import io.renren.modules.user.entity.AgentSettleEntity;
import io.renren.modules.user.service.AgentSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("agentSettleService")
public class AgentSettleServiceImpl extends ServiceImpl<AgentSettleDao, AgentSettleEntity> implements AgentSettleService {

//    @Autowired
//    AgentUserDao agentUserDao;
    /**
     * 代理商每日结算
     */
    public void agentSettle(){

        //查询所有代理
        //agentUserDao.selectList();

        //循环查询每个代理
        //判断是否结算过

       //查询代理的用户的前一天的交易


        //对每一笔结算交易进行结算



    }
}
