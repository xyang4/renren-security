**项目说明** 
- 采用SpringBoot、MyBatis、Shiro框架，开发的一套权限系统，极低门槛，拿来即用。设计之初，就非常注重安全性，为企业系统保驾护航，让一切都变得如此简单。
- 提供了代码生成器，只需编写30%左右代码，其余的代码交给系统自动生成，可快速完成开发任务
<br>

**项目结构** 
```
renren-security
├─ renren-common     公共模块
│ 
├─ renren-api        API服务
│ 
├─ renren-generator  代码生成器
│        └─resources 
│           ├─mapper   MyBatis文件
│           ├─template 代码生成器模板（可增加或修改相应模板）
│           ├─application.yml    全局配置文件
│           └─generator.properties   代码生成器，配置文件
├─ doc  运行脚本及其他文档
├─ db   数据库脚本
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
```
// TODO 待补充
```

<br>

 **项目演示**
- 演示地址：http://demo.open.renren.io/renren-security
- 账号密码：admin/admin


**[工作流及开发流程](https://blog.csdn.net/qq_16912257/article/details/52998295)**
- 从开发版的分支（develop）创建工作分支（feature branches），进行功能的实现或修正
- 工作分支（feature branches）的修改结束后，与开发版的分支（develop）进行合并
- 重复上述❶和❷，不断实现功能直至可以发布
- 创建用于发布的分支（release branches），处理发布的各项工作
- 发布工作完成后与 master 分支合并，打上版本标签（Tag）进行发布
- 如果发布的软件出现 BUG，以打了标签的版本为基础进行修正（hotfixes）


**API 接口清单**

1. 首页 
    - 接单
        - 接单逻辑
        - 订单推送
        - 抢单逻辑
    - 进行中
        - 订单展示处理
2. 充值
    - 充值校验
    - 充值中列表

3. 提现
    - 提现校验
    - 提现中列表
4. 我的
   - 短信验证 √
   - 登录注册 √ （账户信息待补充）
   - 用户 & 账户基本信息
   - 明细
   - 历史账单
   
5. WebSocket 相关
   - 心跳功能        √
    - 抢单处理        √
    - 订单派发        √
    - 取消抢单        √
    - 消息推送        √ 

6. 定时任务
    - 定时推送可抢订单
    - 定时清理 redis 数据
  