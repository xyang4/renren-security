package io.renren.modules.account.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.modules.account.dao.AccountLogDao;
import io.renren.modules.account.entity.AccountLogEntity;
import io.renren.modules.account.service.AccountLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("accountLogService")
public class AccountLogServiceImpl extends ServiceImpl<AccountLogDao, AccountLogEntity> implements AccountLogService {

    @Autowired
    private AccountLogDao accountLogDao;

    @Override
    public List<AccountLogEntity> getAccountLogPageList(Integer userId, Integer pageIndex, Integer pageSize) {
        Page<AccountLogEntity> page = new Page<>(pageIndex,pageSize);
        page.setRecords(accountLogDao.getAccountLogsByUserId(page,userId));
        return page.getRecords();
    }
}
