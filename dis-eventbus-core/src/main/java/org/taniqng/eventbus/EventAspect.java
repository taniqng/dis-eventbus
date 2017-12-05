package org.taniqng.eventbus;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * 使用说明: <br>
 * <p>
 * 发布代理层
 * </p>
 * 
 */
@Aspect
@Component
public class EventAspect {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired DisEventBus bus;

    @Pointcut("@annotation(org.magicframework.eventbus.SendEvent)")
    public void eventPointcut() {}
    
    


    @Around("eventPointcut()")
    public Object doArround(ProceedingJoinPoint joinPoint) throws Throwable {
    	Object obj = joinPoint.proceed();
    	if(obj != null){
    		Method m = ((MethodSignature )joinPoint.getSignature()).getMethod();
    		SendEvent eventAnn = AnnotationUtils.findAnnotation(m, SendEvent.class);
    		logger.debug("发布事件:{}, ", eventAnn.value().getSimpleName());
    		bus.publishEvent(eventAnn.value(), obj);
    	}
    	return obj;
    }


}
