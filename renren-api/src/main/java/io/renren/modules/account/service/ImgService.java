package io.renren.modules.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.account.entity.ImgEntity;

import java.util.Map;

/**
 * 
 *
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-26 13:48:36
 */
public interface ImgService extends IService<ImgEntity> {


    //添加上传图片
    int addImg(ImgEntity imgEntity);
}

