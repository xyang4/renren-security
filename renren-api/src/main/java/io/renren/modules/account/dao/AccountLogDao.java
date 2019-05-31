package io.renren.modules.account.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.renren.modules.account.entity.AccountLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:39:26
 */
@Mapper
public interface AccountLogDao extends BaseMapper<AccountLogEntity> {

    List<AccountLogEntity> getAccountLogsByUserId(Page<AccountLogEntity> page, @Param("userId") Integer userId);
}
