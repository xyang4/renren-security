package io.renren.modules.mer.service.Impl;

import io.renren.common.enums.UserEntityEnum;
import io.renren.modules.mer.service.MerService;
import io.renren.modules.orders.service.OrdersService;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("merService")
public class MerServiceImpl implements MerService {

    @Autowired
    OrdersService ordersService;
    @Autowired
    UserService userService;
    /**
     * 校验商户有效性
     */
    public boolean checkMer(Integer userId){
        UserEntity mer = userService.getById(userId);
        //用户状态有效、是商户类型
        if( mer!=null &&
                mer.getStatus() == UserEntityEnum.Status.VALID.getValue() &&
                mer.getUserType() == UserEntityEnum.UserType.MER.getValue()  ){
            return true;
        }
        return false;
    }

}
