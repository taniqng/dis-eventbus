package org.taniqng.eventbus.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.taniqng.eventbus.MessageListener;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

@MessageListener
public class DefaultEventListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Subscribe
	public void apply(DeadEvent event){
		logger.info("消息被丢弃.");
	}
}
