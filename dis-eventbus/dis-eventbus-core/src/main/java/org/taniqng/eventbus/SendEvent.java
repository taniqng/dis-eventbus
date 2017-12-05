package org.taniqng.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.taniqng.eventbus.api.DisEvent;

/**
 * 
 * 声明一个分布式事件
 * 注解在方法上：会将方法的返回值作为事件对象发送到事件中央路由，由中央路由根据订阅关系发送到订阅它的Queue里，等待需要它的程序去消费。
 * 注解到类上：类中的每一个方法都将触发事件。（暂不支持）
 * @author Yang Tianyou
 *
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@MessageMapping
@Documented
public @interface SendEvent {
	Class<? extends DisEvent<?>> value();
}  
