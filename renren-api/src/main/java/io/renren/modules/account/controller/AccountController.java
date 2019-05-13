package io.renren.modules.account.controller;

import io.renren.modules.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:22:50
 */
@RestController
@RequestMapping("goods/account")
public class AccountController {
    @Autowired
    private AccountService accountService;




//    /**
//     * 信息
//     */
//    @RequestMapping("/info/{accountId}")
//    @RequiresPermissions("goods:account:info")
//    public R info(@PathVariable("accountId") Integer accountId){
//        AccountEntity account = accountService.getById(accountId);
//
//        return R.ok().put("account", account);
//    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    @RequiresPermissions("goods:account:save")
//    public R save(@RequestBody AccountEntity account){
//        accountService.save(account);
//
//        return R.ok();
//    }
//
//    /**
//     * 修改
//     */
//    @RequestMapping("/update")
//    @RequiresPermissions("goods:account:update")
//    public R update(@RequestBody AccountEntity account){
//        ValidatorUtils.validateEntity(account);
//        accountService.updateById(account);
//
//        return R.ok();
//    }
//
//    /**
//     * 删除
//     */
//    @RequestMapping("/delete")
//    @RequiresPermissions("goods:account:delete")
//    public R delete(@RequestBody Integer[] accountIds){
//        accountService.removeByIds(Arrays.asList(accountIds));
//
//        return R.ok();
//    }

}
