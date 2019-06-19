package io.renren.modules.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author Mark
 * @email 18610450436@163.com
 * @date 2019-06-18 17:49:43
 */
@Data
@TableName("chat_msg")
public class ChatMsgEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 聊天信息编号
	 */
	@TableId
	private Integer chatMsgId;
	/**
	 * 发送者
	 */
	private Integer sendUserId;
	/**
	 * 接收者
	 */
	private Integer recvUserId;
	/**
	 * 消息类型 1文字2图片
	 */
	private Integer msgType;
	/**
	 * 聊天信息
	 */
	private String charText;
	/**
	 * 创建时间
	 */
	private String createTime;

}
