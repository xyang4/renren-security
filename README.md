**项目说明** 
- 采用SpringBoot、MyBatis、Shiro框架，开发的一套权限系统，极低门槛，拿来即用。设计之初，就非常注重安全性，为企业系统保驾护航，让一切都变得如此简单。
- 提供了代码生成器，只需编写30%左右代码，其余的代码交给系统自动生成，可快速完成开发任务
<br>

**具有如下特点** 
- 灵活的权限控制，可控制到页面或按钮，满足绝大部分的权限需求
- 完善的部门管理及数据权限，通过注解实现数据权限的控制
- 完善的XSS防范及脚本过滤，彻底杜绝XSS攻击
- 支持分布式部署，session存储在redis中
- 友好的代码结构及注释，便于阅读及二次开发
- 引入quartz定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能
- 页面交互使用Vue2.x，极大的提高了开发效率
- 引入swagger文档支持，方便编写API接口文档

<br>

**项目结构** 
```
renren-security
├─renren-common     公共模块
│ 
├─renren-api        API服务
│ 
├─renren-generator  代码生成器
│        └─resources 
│           ├─mapper   MyBatis文件
│           ├─template 代码生成器模板（可增加或修改相应模板）
│           ├─application.yml    全局配置文件
│           └─generator.properties   代码生成器，配置文件
│
```

<br>

 **技术选型：** 
- 核心框架：Spring Boot 2.1
- 安全框架：Apache Shiro 1.4
- 视图框架：Spring MVC 5.0
- 持久层框架：MyBatis 3.5
- 定时器：Quartz 2.3
- 数据库连接池：Druid 1.1
- 日志管理：SLF4J 1.7、Log4j
- 页面交互：Vue2.x

<br>

 **软件需求** 
- JDK1.8
- MySQL5.5+
- Maven3.0+

<br>

 **本地部署**
- 通过git下载源码
- idea、eclipse需安装lombok插件，不然会提示找不到entity的get set方法
- 创建数据库renren_security，数据库编码为UTF-8
- 执行db/mysql.sql文件，初始化数据【按需导入表结构及数据】
- 修改application-dev.yml文件，更新MySQL账号和密码
- 在renren-security目录下，执行mvn clean install
- Eclipse、IDEA运行ApiApplication.java，则可启动项目【renren-api】
- renren-api访问路径：http://localhost:8081/renren-api/swagger-ui.html
<br>
- Eclipse、IDEA运行GeneratorApplication.java，则可启动项目【renren-generator】
- renren-generator访问路径：http://localhost:8082/renren-generator

<br>

 **集群部署**
- 集群部署，需要安装redis，并配置redis信息
- 需要配置【renren.redis.open=true】，表示开启redis缓存
- 需要配置【renren.cluster=true】，表示开启集群环境

<br>

 **项目演示**
- 演示地址：http://demo.open.renren.io/renren-security
- 账号密码：admin/admin

<br>

**如何交流、反馈、参与贡献？** 
- 开发文档：https://www.renren.io/guide/security
- 官方社区：https://www.renren.io/community
- gitee仓库：https://gitee.com/renrenio/renren-security
- github仓库：https://github.com/renrenio/renren-security
- [人人开源](https://www.renren.io)：https://www.renren.io   

**接口文档效果图：** 
![输入图片说明](http://cdn.renren.io/img/c8dae596146248d8b4d0639738c2932b "在这里输入图片标题")

<br>

**Layui主题风格：**
![输入图片说明](http://cdn.renren.io/img/1013aa91fe8542b7b05d82bc9444433a "在这里输入图片标题")

<br>

**AdminLTE主题风格：**
![输入图片说明](http://cdn.renren.io/img/f9762bc6574545ce908e271995efcf1c "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/a1b8bf1ea3db4844a8652a9cf84048cc "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/e542060605f94b3ebec699b0afffc22d "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/c94be5b4bf0d4387b18e119c91b1a986 "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/ae8c683a01c74d8dbc52d62547efda31 "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/ca38bcf3717c427d82dd67d86b744e18 "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/4862ec46a9ad469b90c30788c4707e35 "在这里输入图片标题")
![输入图片说明](http://cdn.renren.io/img/5d8e7243d30a4421b90f15394b6d1ccd "在这里输入图片标题")

<br>

**[工作流及开发流程](https://blog.csdn.net/qq_16912257/article/details/52998295)**
- 从开发版的分支（develop）创建工作分支（feature branches），进行功能的实现或修正
- 工作分支（feature branches）的修改结束后，与开发版的分支（develop）进行合并
- 重复上述❶和❷，不断实现功能直至可以发布
- 创建用于发布的分支（release branches），处理发布的各项工作
- 发布工作完成后与 master 分支合并，打上版本标签（Tag）进行发布
- 如果发布的软件出现 BUG，以打了标签的版本为基础进行修正（hotfixes）