package com.github.taniqng.eventbus.core;

import com.github.taniqng.eventbus.EventHandler;
import com.github.taniqng.eventbus.api.DisEvent;

public interface EventListener {
	
	@EventHandler
    public <T> void receiveEvent(DisEvent<T> task);

}
