package org.taniqng.eventbus.core;

import org.springframework.util.StringUtils;

public class EventQueueGenerator {

	/**
	 * @return
	 */
	public static String gen(String eventCode) {
		String appName = PropertiesLoaderForBus
				.getProperty("spring.application.name");
		if (StringUtils.isEmpty(appName)) {
			throw new RuntimeException(
					"未找到properties \"spring.application.name\"");
		}
		// 一个应用有一个queue即可，不管它部署几个节点
		return ("QUEUE_EVENTBUS_" + appName +"_"+ eventCode).toUpperCase();
	}
}
