package io.renren.modules.account.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.DateUtils;
import io.renren.modules.account.dao.AccountLogDao;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.entity.AccountLogEntity;
import io.renren.modules.account.service.AccountLogService;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.orders.entity.OrdersEntity;
import io.renren.modules.orders.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service("accountLogService")
public class AccountLogServiceImpl extends ServiceImpl<AccountLogDao, AccountLogEntity> implements AccountLogService {

    @Autowired
    private AccountLogDao accountLogDao;
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AccountService accountService;

    @Override
    public List<AccountLogEntity> getAccountLogPageList(Integer userId, Integer pageIndex, Integer pageSize) {
        Page<AccountLogEntity> page = new Page<>(pageIndex,pageSize);
        page.setRecords(accountLogDao.getAccountLogsByUserId(page,userId));
        return page.getRecords();
    }

    @Override
    public void addAccountLog(Integer userId, Integer orderId, Integer flowType, String flow, BigDecimal amount) {
       OrdersEntity ordersEntity =  ordersService.getById(orderId);
       if(ordersEntity == null){
           return;
       }
       AccountEntity accountEntity =  accountService.getByUserId(userId);
       if(accountEntity == null){
           return;
       }
       AccountLogEntity accountLogEntity = new AccountLogEntity();
       accountLogEntity.setUserId(userId);
       accountLogEntity.setOrderId(orderId);
       accountLogEntity.setOrderType(ordersEntity.getOrderType());
       accountLogEntity.setFlowType(flowType);
       accountLogEntity.setFlow(flow);
       accountLogEntity.setAmount(amount);
       accountLogEntity.setBalance(accountEntity.getBalance());
       accountLogEntity.setCanuseAmount(accountEntity.getCanuseAmount());
       accountLogEntity.setFrozenAmount(accountEntity.getFrozenAmount());
       accountLogEntity.setCreateTime(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
       this.save(accountLogEntity);
    }
}
