package com.github.taniqng.eventbus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.taniqng.eventbus.EventCode;
import com.github.taniqng.eventbus.LocalEventBus;
import com.github.taniqng.eventbus.api.DisEvent;

@EventCode("disevent")
public class DefEventListener implements EventListener {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void receiveEvent(String task) {
		DisEvent<?> event = null;
		try{
			event = (DisEvent<?>)JsonUtils.readObject(task);
		} catch(Exception e){
			logger.debug("不能识别的事件：{}", e.getMessage());
			return;
		}
		logger.debug("处理来自{}[{}]的消息:eventCode-{}", event.getAppId(),
				event.getSourceIp(), event.getEventCode());
		logger.debug("消息内容：{}", event.getData());
		LocalEventBus.publish(event);
	}

}
