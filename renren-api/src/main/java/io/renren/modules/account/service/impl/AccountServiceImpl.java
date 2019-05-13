package io.renren.modules.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.account.dao.AccountDao;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.service.AccountService;
import org.springframework.stereotype.Service;


@Service("accountService")
public class AccountServiceImpl extends ServiceImpl<AccountDao, AccountEntity> implements AccountService {


}
