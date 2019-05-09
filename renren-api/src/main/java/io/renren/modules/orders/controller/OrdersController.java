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
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-08 17:36:57
 */
@RestController
@RequestMapping("goods/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = ordersService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{orderId}")
//    @RequiresPermissions("goods:orders:info")
//    public R info(@PathVariable("orderId") Integer orderId){
//        OrdersEntity orders = ordersService.getById(orderId);
//
//        return R.ok().put("orders", orders);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    @RequiresPermissions("goods:orders:save")
//    public R save(@RequestBody OrdersEntity orders){
//        ordersService.save(orders);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    @RequiresPermissions("goods:orders:update")
//    public R update(@RequestBody OrdersEntity orders){
//        ValidatorUtils.validateEntity(orders);
//        ordersService.updateById(orders);
//
//        return R.ok();
//    }


}
