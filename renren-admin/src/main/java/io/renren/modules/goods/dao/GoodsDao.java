package io.renren.modules.goods.dao;

import io.renren.modules.goods.entity.GoodsEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品管理
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-04-26 14:07:04
 */
@Mapper
public interface GoodsDao extends BaseMapper<GoodsEntity> {
	
}
