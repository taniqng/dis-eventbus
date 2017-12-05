package org.taniqng.eventbus.helper;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.stereotype.Component;


/**
 * 
 * MQ资源注册器
 * @author Yang Tianyou
 *
 */
@Component
public class MQResourceRegistry implements BeanFactoryAware {
	
	private BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	public Queue registQueue(String queueName){
		Queue queue = new Queue(queueName);
		((ConfigurableBeanFactory) this.beanFactory).registerSingleton(queueName, queue);
		return queue;
	}
	
	public Exchange registExchange(String exName, String type){
		Exchange exchange = new ExchangeBuilder(exName, type).build();
		((ConfigurableBeanFactory) this.beanFactory).registerSingleton(exName, exchange);
		return exchange;
	}
	
	public Binding registBinding(Queue queue, Exchange ex, String rk, Map<String, Object> map){
		Binding bind = BindingBuilder.bind(queue).to(ex).with(rk).and(map);
		((ConfigurableBeanFactory) this.beanFactory).registerSingleton(queue.getName()+"_"+ex.getName()+"_"+rk, bind);
		return bind;
	}
	
	public Binding registBindingWithName(String queueName, String exName, String rk){
		Binding bind = new Binding(queueName, DestinationType.QUEUE, exName, rk, new HashMap<String, Object>());
		((ConfigurableBeanFactory) this.beanFactory).registerSingleton(queueName+"_"+exName+"_"+rk, bind);
		return bind;
	}
	
}
