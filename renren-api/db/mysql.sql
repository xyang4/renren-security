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
