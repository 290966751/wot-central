# wot-central
基础架构WATCH OUT



## 如果您觉得有帮助，请点右上角 "Star" 支持一下谢谢


## 1. 项目介绍
* Github地址：https://github.com/290966751/wot-central
* 提供应用管理，方便第三方系统接入
* 引入组件化的思想实现高内聚低耦合并且高度可配置化
* 注重代码规范，严格控制包依赖，每个工程基本都是最小依赖
* 非常适合学习和企业中使用
>重构于开源项目：https://github.com/zlt2000/microservices-platform

## 2. 模块说明
```lua
wot-central -- 父项目，公共依赖版本管理
├─demo -- demo模块
├─doc -- 项目文档、sql记录、升级记录
│  └─sql
├─wot -- 打包示例目录
│  └─xxl-job
│      └─xxl-job-admin
├─wot-commons -- 通用工具一级工程
│  ├─wot-common-core -- 公共基础组件
│  ├─wot-common-entity -- 通用实体 | 相关枚举、实体存放
│  ├─wot-common-spring-boot-starter -- spring boot通用组件
│  └─wot-redis-spring-boot-starter -- redis通用组件
├─wot-job -- 任务调度一级工程
│  ├─job-common-core -- 任务调度通用操作逻辑
│  └─job-xxl-job --- xxl-job一级工程，可选
│      ├─xxl-job-admin -- xxl-job 调度中心
│      ├─xxl-job-core -- xxl-job 公共依赖
│      └─xxl-job-executor-sample-springboot -- xxl-job Springboot管理执行器
└─wot-web -- 后端接口一级工程
```
## 3. 交流反馈
* 欢迎提交`ISSUS`，请写清楚问题的具体原因，重现步骤和环境(上下文)
