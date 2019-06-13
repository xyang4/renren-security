package io.renren.modules.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.account.entity.AccountLogEntity;
import io.renren.modules.orders.entity.OrdersEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:39:26
 */
public interface AccountLogService extends IService<AccountLogEntity> {


    List<AccountLogEntity> getAccountLogPageList(Integer userId, Integer pageIndex, Integer pageSize);

    void addAccountLog(Integer userId, Integer orderId, Integer flowType, String flow, BigDecimal amount);
}

