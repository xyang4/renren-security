package io.renren.modules.orders.controller;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.common.validator.ValidatorUtils;
import io.renren.modules.orders.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * app订单接口
 */
@RestController
@RequestMapping("app/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;




}
