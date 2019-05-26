package io.renren.modules.account.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-05-26 13:48:36
 */
@Data
@TableName("img")
public class ImgEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 图片编号
	 */
	@TableId
	private Integer imgId;
	/**
	 * 图片base64
	 */
	private String base64;
	/**
	 * 创建时间
	 */
	private String createTime;

}
