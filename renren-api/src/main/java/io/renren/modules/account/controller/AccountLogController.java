package io.renren.modules.account.controller;

import io.renren.modules.account.service.AccountLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:39:26
 */
@RestController
@RequestMapping("goods/accountlog")
public class AccountLogController {
    @Autowired
    private AccountLogService accountLogService;

//    /**
//     * 列表
//     */
//    @RequestMapping("/list")
//    @RequiresPermissions("goods:accountlog:list")
//    public R list(@RequestParam Map<String, Object> params){
//        PageUtils page = accountLogService.queryPage(params);
//
//        return R.ok().put("page", page);
//    }
//
//
//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{accountLogId}")
//    @RequiresPermissions("goods:accountlog:info")
//    public R info(@PathVariable("accountLogId") Integer accountLogId){
//        AccountLogEntity accountLog = accountLogService.getById(accountLogId);
//
//        return R.ok().put("accountLog", accountLog);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    @RequiresPermissions("goods:accountlog:save")
//    public R save(@RequestBody AccountLogEntity accountLog){
//        accountLogService.save(accountLog);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    @RequiresPermissions("goods:accountlog:update")
//    public R update(@RequestBody AccountLogEntity accountLog){
//        ValidatorUtils.validateEntity(accountLog);
//        accountLogService.updateById(accountLog);
//
//        return R.ok();
//    }



}
