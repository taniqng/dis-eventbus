package com.github.taniqng.eventbus.core;

import com.github.taniqng.eventbus.EventHandler;

public interface EventListener {
	
	@EventHandler
    public void receiveEvent(String task);

}
