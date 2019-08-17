-- 用户表
CREATE TABLE `tb_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `mobile` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(64) COMMENT '密码',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户';

-- 用户Token表
CREATE TABLE `tb_token` (
  `user_id` bigint NOT NULL,
  `token` varchar(100) NOT NULL COMMENT 'token',
  `expire_time` datetime COMMENT '过期时间',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户Token';

-- 账号：13612345678  密码：admin
INSERT INTO `tb_user` (`username`, `mobile`, `password`, `create_time`) VALUES ('mark', '13612345678', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', '2017-03-23 22:37:41');

CREATE TABLE `sys_dict` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(20) NOT NULL,
  `val` text,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `type` tinyint(2) DEFAULT NULL COMMENT '类型 1 系统相关 2 用户相关',
  `version` int(11) DEFAULT NULL,
  `status` tinyint(2) DEFAULT '1' COMMENT '状态 1 正常 2 禁用 3 已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='系统字典表';

CREATE TABLE `user_sms_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(15) NOT NULL COMMENT '发送者手机号',
  `client_ip` varchar(20) DEFAULT NULL COMMENT '请求IP',
  `type` tinyint(2) DEFAULT '1' COMMENT '业务类型: 1 注册 2找回密码 3登录 4其他',
  `code` varchar(10) DEFAULT NULL COMMENT '验证码',
  `remark` varchar(20) DEFAULT NULL COMMENT '备注,e.g.失败原因',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(2) DEFAULT '1' COMMENT '1:成功 2:失败',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 COMMENT='短信发送记录表';

CREATE TABLE `agent_settle_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `agent_id` int(11) DEFAULT NULL COMMENT '代理id',
  `settle_type` tinyint(2) DEFAULT NULL COMMENT '结算订单类型:1搬运工充值2搬运工提现3商户充值4商户提现',
  `settle_date` varchar(10) DEFAULT NULL COMMENT '结算日期',
  `charge_rate` decimal(10,4) DEFAULT '0.0000' COMMENT '代理费率',
  `settle_amount` decimal(10,2) DEFAULT '0.00' COMMENT '结算金额',
  `settle_order_num` int(11) DEFAULT '0' COMMENT '结算订单数量',
  `settle_user_num` int(11) DEFAULT '0' COMMENT '结算下级代理人数',
  `settle_profit` decimal(10,2) DEFAULT '0.00' COMMENT '结算收益',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `settle_record` text COMMENT '计算过程：由userId:amount:charge_rate拼接',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_settlt_date` (`agent_id`,`settle_type`,`settle_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理结算收益记录表';

CREATE TABLE `agent_settle_user_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '代理',
  `order_type` tinyint(2) DEFAULT NULL COMMENT '订单类型:1搬运工充值2搬运工提现3商户充值4商户提现',
  `settle_date` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '结算日期',
  `charge_rate` decimal(10,4) DEFAULT '0.0000' COMMENT '收益率',
  `num` int(11) DEFAULT '0' COMMENT '接单数量',
  `amount` decimal(10,2) DEFAULT '0.00' COMMENT '接单金额',
  `profit` decimal(10,2) DEFAULT '0.00' COMMENT '收益',
  `agent_id` int(11) DEFAULT NULL COMMENT '上级代理id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unq_user_date` (`user_id`,`order_type`,`settle_date`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8 COMMENT='代理每日收益记录';

# 存储过程
DELIMITER $$
USE `pay`$$
DROP PROCEDURE IF EXISTS `exec_user_recv_report`$$
/**
 * @desc 报表统计用户每天接单数及接单金额
 * @author: xyang
 */
CREATE DEFINER=`root`@`%` PROCEDURE `exec_user_recv_report`(IN settleDate VARCHAR(10))
BEGIN
SET settleDate=IFNULL(settleDate,CURRENT_DATE());
INSERT INTO  agent_settle_user_record(user_id,agent_id,order_type,charge_rate,settle_date,num,amount,profit,create_time)
	SELECT
	 au.`USER_ID` userId,au.`AGENT_ID` agentId,IFNULL(ags.order_type,3),au.`RECV_CHARGE_RATE` chargeRate,IFNULL(ags.settleDate,settleDate),
	 IFNULL(ags.recv_num,0) recvNum,IFNULL(ags.totalAmount,0) totalRecvAmount,
	 TRUNCATE(IFNULL(ags.totalAmount*au.`RECV_CHARGE_RATE`,0),2) totalAgentSettle ,
	 NOW()
	FROM
	 agent_user au
	LEFT JOIN (
		SELECT
		  o.`RECV_USER_ID` user_id,o.order_type, o.`ORDER_DATE` settleDate,
		  COUNT(1) recv_num, SUM(IFNULL(o.`RECV_AMOUNT`,0)) totalAmount
		FROM
		  orders o
		WHERE o.`ORDER_TYPE` = 3  AND o.`ORDER_STATE` = 9 AND o.recv_user_id IS NOT NULL AND o.`ORDER_DATE`= settleDate
		GROUP BY o.`RECV_USER_ID`,o.order_type,settleDate
	) ags ON au.`USER_ID` = ags.user_id;
    END$$

DELIMITER ;
