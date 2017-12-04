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
- Java 8

## 基于注解的事件发布

```
	/**
	 * 定义事件对象
	 */
	public class SendPostEvent extends DisEvent<PostInfo>{

		private static final long serialVersionUID = -138646642993478976L;

	}

```

```
	/**
	 * 保存文章
	 */
	@RequestMapping(value = "/posts", method = RequestMethod.POST)
	@SendEvent(SendPostEvent.class)
	public PostInfo savePost(@Valid @NotNull @RequestBody PostInfo post) {
		//你的业务代码
		//返回文章对象, 该对象作为事件发布的data, 可以被事件监听者接收。
	}

```

