# dis-eventbus
Simple distribute eventbus 
based on：
* spring boot
* rabbitMQ
* google guava

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

## 事件监听器

```
/**
 * 监听PostInfo的变化，同步更新Elasticsearch。<br>
 * 这里可以考虑使用缓冲队列，然后bulk操作来批量处理，后期优化。<br>
 * 
 * <p>
 *  注：对单机而言这个操作于主业务来说是异步的。对分布式环境更是会选择最空闲的节点来执行。<br>
 *      所以注意使用合适的锁来保证对共享资源操作时的同步。
 * </p>
 */
@MessageListener
public class PostInfoListener {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	SearchService postService;
	
	@Subscribe	
	public void accept(SendPostEvent event){
	  PostInfo postInfo = event.getData();	  
	  //将postInfo保存至ES
	}
	
	@Subscribe
	public void accept(EditPostEvent event){
	  PostInfo postInfo = event.getData();	  
	  //更新ES指定的document
	}
}

```
## 遗留问题
* 将考虑抽象成SPI，使其脱离MQ机制, 使依赖反转（使dis-eventbus本身不依赖于MQ机制, 相反MQ仅作为dis-eventbus的一个默认的provider）。

## Q & A
> Q: 项目中使用activeMQ如何操作？<br>
> A: dis-eventbus使用spring-boot-starter-amqp, 默认使用rabbitMQ作为AMQP的provider，只需execlude掉spring-rabbit，实现自己的amqp provider。

## License
dis-eventbus is Open Source software released under the
http://www.apache.org/licenses/LICENSE-2.0.html[Apache 2.0 license].
