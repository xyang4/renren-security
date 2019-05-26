package io.renren.modules.account.service.impl;

import io.renren.modules.account.dao.ImgDao;
import io.renren.modules.account.entity.ImgEntity;
import io.renren.modules.account.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;



@Service("imgService")
public class ImgServiceImpl extends ServiceImpl<ImgDao, ImgEntity> implements ImgService {

    @Autowired
    private ImgDao imgDao;

    @Override
    public int addImg(ImgEntity imgEntity) {
        return imgDao.insert(imgEntity);
    }
}
