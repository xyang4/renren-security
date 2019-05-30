package io.renren.modules.account.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.R;
import io.renren.modules.account.entity.AccountEntity;
import io.renren.modules.account.service.AccountService;
import io.renren.modules.account.service.PayChannelService;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.user.entity.TokenEntity;
import io.renren.modules.user.entity.UserEntity;
import io.renren.modules.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:22:50
 */
@AppLogin
@RestController
@RequestMapping("app/account")
@Api("账户相关")
public class AccountController extends BaseController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private IUserService userService;
    @Autowired
    private PayChannelService payChannelService;


    /**
     * 账户信息接口
     * @return
     */
    @AppLogin
    @ApiOperation("账户信息接口")
    @RequestMapping("/userInfo")
    public R accountInfo(){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //查询账户和用户信息
        UserEntity userEntity = userService.getById(tokenEntity.getUserId());
        AccountEntity accountEntity = accountService.getByUserId(tokenEntity.getUserId());
        if(userEntity == null || accountEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        Map<String,Object> rMap = new HashMap<>();
        rMap.put("user",userEntity);
        rMap.put("account",accountEntity);
        rMap.put("wxqr",0);
        rMap.put("aliqr",0);
        rMap.put("bank",0);
        //查询用户支付方式信息
        List<Map<String,Object>> payChannelMap = payChannelService.getPayChannelGroupData(tokenEntity.getUserId());
        if(payChannelMap != null){
            for (Map<String, Object> map : payChannelMap) {
                if("wxqr".equals(map.get("payType"))){
                    rMap.put("wxqr",map.get("count"));
                }else if("aliqr".equals(map.get("payType"))){
                    rMap.put("aliqr",map.get("count"));
                }else if("bank".equals(map.get("payType"))){
                    rMap.put("bank",map.get("count"));
                }
            }
        }
        return R.ok(rMap);
    }

// TODO 账户信息查询

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
