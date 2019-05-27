package io.renren.modules.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import io.renren.modules.account.dao.AccountDao;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountDao, AccountEntity> implements AccountService {

    @Autowired
    private AccountDao accountDao;

    /**
     * 全面校验用户账户：所有账户状态校验
     */
    public boolean overallCheckUserAccount(AccountEntity accountEntity){
        Map<String ,Object> retMap = new HashMap<>();
        //检查账户状态 状态：-1-冻结 0-停用1启用
        if(accountEntity.getStatus()!=1){
            return false;
        }
        //检查激活状态 激活状态:0-未激活1-激活
        if(accountEntity.getActiveStatus()!=1){
            return false;
        }
        //检查发单状态:0-发单1-正常发单
        if(accountEntity.getSendStatus()!=1){
            return false;
        }
        //检查接单状态:0-禁止接单1-正常接单2-禁止接单1天 3-禁止接单2小时
        if(accountEntity.getRecvStatus()!=1){
            return false;
        }
        return true;
    }

    @Override
    public int updateAmount(Integer userId, BigDecimal canuseAmount, BigDecimal frozenAmount) {
        Map<String,Object> param =new HashMap<>();
        param.put("userId",userId);
        param.put("canuseAmount",canuseAmount);
        param.put("frozenAmount",frozenAmount);
        return accountDao.updateAmount(param);
    }

    @Override
    public AccountEntity getByUserId(Integer userId) {
        return accountDao.getByUserId(userId);
    }
}
