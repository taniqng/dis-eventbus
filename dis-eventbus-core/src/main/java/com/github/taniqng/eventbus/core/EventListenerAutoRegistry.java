package com.github.taniqng.eventbus.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import com.github.taniqng.eventbus.LocalEventBus;
import com.github.taniqng.eventbus.MessageListener;

@Component
public class EventListenerAutoRegistry implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if(AnnotationUtils.findAnnotation(bean.getClass(), MessageListener.class)!=null){
			LocalEventBus.register(bean);
		}
		
		return bean;
	}

}
