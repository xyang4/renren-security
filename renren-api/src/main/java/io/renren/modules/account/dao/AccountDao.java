package io.renren.modules.account.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.account.entity.AccountEntity;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:22:50
 */
@Mapper
public interface AccountDao extends BaseMapper<AccountEntity> {

    int updateAmount(Map<String, Object> param);

    AccountEntity getByUserId(Integer userId);
}
