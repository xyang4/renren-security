/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.renren.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.modules.user.entity.UserEntity;

/**
 * 用户
 *
 * @author Mark sunlightcs@gmail.com
 */
public interface UserService extends IService<UserEntity> {

    UserEntity queryByMobile(String mobile);

    Integer registeredQuickly(String mobile);
}
