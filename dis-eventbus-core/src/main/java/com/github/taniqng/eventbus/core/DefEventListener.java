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
	public <T> void receiveEvent(DisEvent<T> task) {
		logger.debug("处理来自{}[{}]的消息:eventCode-{}", task.getAppId(),
				task.getSourceIp(), task.getEventCode());
		logger.debug("消息内容：{}", task.getData());
		LocalEventBus.publish(task);
	}

}
