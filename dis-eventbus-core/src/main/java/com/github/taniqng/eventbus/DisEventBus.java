package com.github.taniqng.eventbus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.taniqng.eventbus.api.DisEvent;
import com.github.taniqng.eventbus.core.Destinations;
import com.github.taniqng.eventbus.core.JsonUtils;
import com.github.taniqng.eventbus.core.PropertiesLoaderForBus;

@Component
public class DisEventBus {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired RabbitTemplate rabbitTemplate;
	
	@SuppressWarnings("unchecked")
	protected <T> void publishEvent(Class<? extends DisEvent<T>> eventClass, Object event){
		try{
			T evn = (T) event;
			publish(eventClass, evn);
		} catch(ClassCastException e1) {
			logger.error("{}类型需传入的数据不符",eventClass.getName());
		} 
	}
	
	/**
	 * 发布异常事件
	 */
	public <T> void publish(Class<? extends DisEvent<T>> eventClass, T event){
		try {
			DisEvent<T> ev = eventClass.newInstance();
			ev.setAppId(getAppId());
			ev.setEventCode("");
			ev.setSourceIp(getIpAddr());
			ev.setEventFlow(UUID.randomUUID().toString());
			ev.setData(event);
			rabbitTemplate.convertAndSend(Destinations.DEST_TANIQNG_FANOUT_EVENTBUS.getExchange(), 
					Destinations.DEST_TANIQNG_FANOUT_EVENTBUS.getRoutingKey(),
					JsonUtils.writeObject(ev));
		} catch (Exception e) {
			logger.error("不能创建事件实例，猜测{}没有提供默认无参构造器.", eventClass.getSimpleName());
		}
		
	}
	
	private String getIpAddr(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getAppId(){
		String appName = PropertiesLoaderForBus
				.getProperty("spring.application.name");
		if (StringUtils.isEmpty(appName)) {
			throw new RuntimeException(
					"未找到properties \"spring.application.name\"");
		}
		return appName;
	}
}
