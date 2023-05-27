# web-chat

我想说一下为什么会有这个项目，之前在深圳的一家公司上班，项目中需要有一个客服聊天功能，我提了我认为的现在做的这个项目的相关优点给项目经理，但他没有采纳，他最终用了activemq作为消息传递的方式进行聊天……但我认为我的方案应该不错，所以就做了一个聊天项目。

一、安装相关
1、chat-root
	此为JAVA微服务项目，里面有多个子项目，项目启动前需要安装maven、JDK1.8以上、redis、zookeeper、mysql并启动(maven、redis、zookeeper、mysql)相关服务

2、chatpro
	1>此为VUE项目，使用webpack 3.6.0框架。项目环境需要安装node.js v16以上和vue.cli 脚手架v2.0以上。
	2>启动项目前先在项目目录下命令行执行npm install安装所有需要的包
	
3、db0.sql
	此为用户和聊天需要用的基本表，数据库名：db0（如果修改，则同步修改chat-core项目application.properties文件里面指定的数据库名）
	
4、db1.sql 
	此为用户日记功能使用到的相关表，数据库名：db1
	
二、项目/功能简介
1、后台框架主要使用spring boot + mybatis + redis + shardingsphere(分库分表) + dubbo微服务；

2、主要功能：聊天（用户一对一、客服一对多、群聊）、功能权限、登录验证/绑定、日记、信息加密处理;

3、聊天信息使用redis进行缓存（这是我做这个项目的初衷）

4、项目使用到了别的一些UP主的代码，主要有：\n
	1)泡泡球功能--来自网络kmh0228
	
三、效果图如下：
	![002](https://github.com/fangg2000/web-chat/assets/131591502/f6f16e28-c856-412b-9f7c-2bbba9cef251)
	![004](https://github.com/fangg2000/web-chat/assets/131591502/10dec65b-a910-4417-8725-46f67da16feb)
	![006](https://github.com/fangg2000/web-chat/assets/131591502/486bd8b6-0101-4337-befa-537b084b7894)

最后，这个项目花了我差不多半年时光，支持一下吧…
