package io.renren.modules.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.account.entity.AccountEntity;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:22:50
 */
public interface AccountService extends IService<AccountEntity> {

    /**
     * 全面校验用户账户：所有账户状态校验
     */
    boolean overallCheckUserAccount(AccountEntity accountEntity);

    /**
     * 更改账户金额
     * @param userId
     * @param canuseAmount 可用余额，带符号
     * @param frozenAmount 冻结金额，带符号
     * @param balance 余额，带符号
     * @return
     */
    int updateAmount(Integer userId, BigDecimal canuseAmount, BigDecimal frozenAmount,BigDecimal balance);

    /**
     * 根据userId查询用户
     * @param userId
     * @return
     */
    AccountEntity getByUserId(Integer userId);
}

