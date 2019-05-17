package io.renren.modules.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.account.entity.AccountEntity;

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
    public boolean overallCheckUserAccount(AccountEntity accountEntity);

}

