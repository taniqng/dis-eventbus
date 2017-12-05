package org.taniqng.eventbus.core;

import org.taniqng.eventbus.EventHandler;
import org.taniqng.eventbus.api.DisEvent;

public interface EventListener {
	
	@EventHandler
    public <T> void receiveEvent(DisEvent<T> task);

}
