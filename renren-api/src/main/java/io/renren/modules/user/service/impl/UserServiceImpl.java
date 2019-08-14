/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.enums.UserEntityEnum;
import io.renren.common.exception.RRException;
import io.renren.common.utils.DateUtils;
import io.renren.common.utils.R;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.system.service.IConfigService;
import io.renren.modules.user.dao.AgentUserDao;
import io.renren.modules.user.dao.UserDao;
import io.renren.modules.user.entity.AgentUserEntity;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.IUserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements IUserService {

    @Autowired
    private AgentUserDao agentUserDao;
    @Autowired
    private IConfigService configService;
    @Override
    public UserEntity queryByMobile(String mobile) {
        return baseMapper.selectOne(new QueryWrapper<UserEntity>().eq("mobile", mobile));
    }

//    @Override
//    public Integer registeredQuickly(String mobile) {
//        UserEntity entity = new UserEntity();
//        // TODO 账户初始化
//        entity.setMobile(mobile);
//        entity.setPasswd(DigestUtils.sha256Hex(mobile.substring(mobile.length() - 7)));
//        save(entity);
//        return entity.getUserId();
//    }

    /**
     * 全面校验用户：所有用户状态校验
     */
    public boolean overallCheckUser(UserEntity userEntity) {
        //校验用户状态
        return userEntity.getStatus() == 1;
    }

    @Autowired
    UserDao userDao;

    @Override
    public Map<String, Object> getAccountBaseInfo(Integer userId, String mobile) {
        return userDao.getAccountBaseInfo(userId, mobile);
    }

    /**
     * 添加推荐人
     * @param userId
     * @param mobile
     * @param nickName
     * @param pwd
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public R recommendUser(Integer userId, String mobile, String nickName, String pwd) {
        UserEntity userEntity = new UserEntity();
        userEntity.setPasswd(DigestUtils.sha256Hex(pwd));
        userEntity.setNickName(nickName);
        userEntity.setMobile(mobile);
        userEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        userEntity.setUserType(UserEntityEnum.UserType.PORTER.getValue());
        userEntity.setUserLevel(1);
        userEntity.setStatus(1);
        userEntity.setUserGroup(1);
        int r = userDao.insert(userEntity);
        if(r>0){
            AccountEntity accountEntity = new AccountEntity();
            accountEntity.setUserId(userEntity.getUserId());
            accountEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            boolean rr = SpringContextUtils.getBean(AccountService.class).save(accountEntity);
            if(!rr){
                throw new RRException("添加推荐人失败");
            }
            AgentUserEntity  agentUserEntity = new AgentUserEntity();
            agentUserEntity.setAgentId(userId);
            agentUserEntity.setUserId(userEntity.getUserId());
            agentUserEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            agentUserEntity.setModifyTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
            //添加默认收益费率
            String recvRate = configService.selectConfigByKey("recv_mer_chargeRate");//公共收益率
            agentUserEntity.setRecvChargeRate(recvRate == null ? new BigDecimal(0) : new BigDecimal(recvRate));
            int rrr = agentUserDao.insert(agentUserEntity);
            if(rrr < 1){
                throw new RRException("添加推荐人失败");
            }
        }
        return R.ok();
    }
}
