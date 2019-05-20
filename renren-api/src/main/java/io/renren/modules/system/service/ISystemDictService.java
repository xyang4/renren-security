package io.renren.modules.system.service;

import io.renren.modules.system.entity.SystemDict;

import java.util.List;

public interface ISystemDictService {

    List<SystemDict> list(Byte type, Integer status);

    SystemDict selectByKey(String key);
}
