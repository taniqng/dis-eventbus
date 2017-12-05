package org.taniqng.eventbus;

import com.google.common.eventbus.EventBus;

public class LocalEventBus {
	
	private static final EventBus bus = new EventBus();
	
	public static void publish(Object event){
		bus.post(event);
	}
	
	public static void register(Object listener){
		bus.register(listener);
	}

}
