package io.renren.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.util.StaticConstant;
import io.renren.modules.system.dao.SystemDictDao;
import io.renren.modules.system.entity.SystemDict;
import io.renren.modules.system.service.ISystemDictService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemDictServiceImpl extends ServiceImpl<SystemDictDao, SystemDict> implements ISystemDictService {

    @Override
    public List<SystemDict> list(Byte type, Integer status) {

        QueryWrapper<SystemDict> entityWrapper = new QueryWrapper<>();
        if (null != type) {
            entityWrapper.eq("type", type);
        }
        if (null == status) {
            entityWrapper.eq("status", StaticConstant.DATA_STATUS_NORMAL);
        }
        return list(entityWrapper.orderByAsc("create_time desc"));
    }

    @Override
    public SystemDict selectByKey(String key) {
        QueryWrapper<SystemDict> wrapper = new QueryWrapper<>();
        wrapper.eq("`key`", key).eq("`status`", StaticConstant.DATA_STATUS_NORMAL);
        return getOne(wrapper);
    }

}
