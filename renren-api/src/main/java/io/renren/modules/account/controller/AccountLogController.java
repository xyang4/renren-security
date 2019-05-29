package io.renren.modules.account.controller;

import io.renren.common.annotation.AppLogin;
import io.renren.common.utils.R;
import io.renren.modules.account.entity.AccountLogEntity;
import io.renren.modules.account.service.AccountLogService;
import io.renren.modules.common.controller.BaseController;
import io.renren.modules.user.entity.TokenEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-13 18:39:26
 */
@RestController
@RequestMapping("app/accountlog")
public class AccountLogController extends BaseController {
    @Autowired
    private AccountLogService accountLogService;


    @AppLogin
    @ApiOperation("账户log接口")
    @RequestMapping("/pageList")
    public R logList(@RequestBody Map paramMap){
        TokenEntity tokenEntity = getToken();
        if(tokenEntity == null){
            return R.error(-1,"查询用户信息失败");
        }
        //默认第一页5条
        Integer pageIndex = paramMap.get("pageIndex")==null ? 1 : (Integer) paramMap.get("pageIndex");
        Integer pageSize =  paramMap.get("pageSize")==null ? 5 : (Integer) paramMap.get("pageSize");
        List<AccountLogEntity> accountLogEntityList = accountLogService.getAccountLogPageList(tokenEntity.getUserId(),pageIndex,pageSize);
        return R.ok(accountLogEntityList);
    }

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
