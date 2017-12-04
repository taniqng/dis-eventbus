# dis-eventbus
Simple distribute event bus implemented by RabbitMQ

## 目标：
* 实现一个分布式事件总线。
* 最大限度的方便开发者使用监听者模式构建低耦合、易于扩展的应用程序。
* 能够使用注解发布事件等一系列高效编程方式。

## 使用建议：
* 软件合理分层建模，使事件沿着单一层面传播。避免逆向甚至网状传播。

## 环境

- Maven 3
- Java 8 (the project produces Java 6 compatible bytecode but partially integrates with Java 8)
