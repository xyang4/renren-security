package io.renren.modules.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.account.dao.AccountLogDao;
import io.renren.modules.account.entity.AccountLogEntity;
import io.renren.modules.account.service.AccountLogService;
import org.springframework.stereotype.Service;


@Service("accountLogService")
public class AccountLogServiceImpl extends ServiceImpl<AccountLogDao, AccountLogEntity> implements AccountLogService {


}
