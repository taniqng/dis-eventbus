package com.github.taniqng.eventbus.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.github.taniqng.eventbus.EventCode;
import com.github.taniqng.eventbus.EventHandler;
import com.github.taniqng.eventbus.helper.MQResourceRegistry;



/**
 * <p>
 *  注册EventListener
 * </p>
 * 
 * @author <a href="mailto:sk.your@qq.com">Yang Tianyou</a>
 */
@Component
public class EventListenerPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware,
        BeanClassLoaderAware, EnvironmentAware, SmartInitializingSingleton {

    public static final String DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME =
            "rabbitListenerContainerFactory";

    public static final String RABBIT_EMPTY_STRING_ARGUMENTS_PROPERTY =
            "spring.rabbitmq.emptyStringArguments";

    private static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();

    private final Log logger = LogFactory.getLog(this.getClass());

    private final Set<String> emptyStringArguments = new HashSet<String>();

    private RabbitListenerEndpointRegistry endpointRegistry;

    private String containerFactoryBeanName = DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME;

    private BeanFactory beanFactory;

    private ClassLoader beanClassLoader;

    private final RabbitHandlerMethodFactoryAdapter messageHandlerMethodFactory =
            new RabbitHandlerMethodFactoryAdapter();

    private final RabbitListenerEndpointRegistrar registrar = new RabbitListenerEndpointRegistrar();

    private final AtomicInteger counter = new AtomicInteger();

    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();

    private BeanExpressionContext expressionContext;
    
    //@Autowired private RabbitMQResourceRegistry mqRegistry;
    @Autowired private MQResourceRegistry mqRegistry;
    
    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    public EventListenerPostProcessor() {
        this.emptyStringArguments.add("x-dead-letter-exchange");
    }

    public void setEndpointRegistry(RabbitListenerEndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    public void setContainerFactoryBeanName(String containerFactoryBeanName) {
        this.containerFactoryBeanName = containerFactoryBeanName;
    }

    public void setMessageHandlerMethodFactory(
            MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory
                .setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver =
                    ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext =
                    new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory, null);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        String property =
                environment.getProperty(RABBIT_EMPTY_STRING_ARGUMENTS_PROPERTY, String.class);
        if (property != null) {
            this.emptyStringArguments.addAll(StringUtils.commaDelimitedListToSet(property));
        }
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.registrar.setBeanFactory(this.beanFactory);

        if (this.beanFactory instanceof ListableBeanFactory) {
            Map<String, RabbitListenerConfigurer> instances =
                    ((ListableBeanFactory) this.beanFactory)
                            .getBeansOfType(RabbitListenerConfigurer.class);
            for (RabbitListenerConfigurer configurer : instances.values()) {
                configurer.configureRabbitListeners(this.registrar);
            }
        }

        if (this.registrar.getEndpointRegistry() == null) {
            if (this.endpointRegistry == null) {
                Assert.state(this.beanFactory != null,
                        "BeanFactory must be set to find endpoint registry by bean name");
                this.endpointRegistry =
                        this.beanFactory
                                .getBean(
                                        RabbitListenerConfigUtils.RABBIT_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME,
                                        RabbitListenerEndpointRegistry.class);
            }
            this.registrar.setEndpointRegistry(this.endpointRegistry);
        }

        if (this.containerFactoryBeanName != null) {
            this.registrar.setContainerFactoryBeanName(this.containerFactoryBeanName);
        }

        // Set the custom handler method factory once resolved by the configurer
        MessageHandlerMethodFactory handlerMethodFactory =
                this.registrar.getMessageHandlerMethodFactory();
        if (handlerMethodFactory != null) {
            this.messageHandlerMethodFactory.setMessageHandlerMethodFactory(handlerMethodFactory);
        }

        // Actually register all listeners
        this.registrar.afterPropertiesSet();

    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName)
            throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);

        if(bean instanceof EventListener){
            
            List<Method> handlers = retrieveHandlers(targetClass);
            
            if (handlers.size() > 0) {
                processMultiMethodListeners(handlers.toArray(new Method[handlers.size()]), bean, beanName);
            }
        }
        return bean;
    }

    private List<Method> retrieveHandlers(Class<?> targetClass){
        if(!EventListener.class.isAssignableFrom(targetClass)){
           return Collections.emptyList();  
        }
        List<Method> multiMethods = new ArrayList<Method>();
        ReflectionUtils.doWithMethods(targetClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException,
                    IllegalAccessException {
                EventHandler taskHandler = AnnotationUtils.findAnnotation(method, EventHandler.class);
                if (taskHandler != null) {
                    multiMethods.add(method);
                }
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
        return multiMethods;
    }

    

    private void processMultiMethodListeners(Method[] multiMethods, Object bean, String beanName) {
        List<Method> checkedMethods = new ArrayList<Method>();
        for (Method method : multiMethods) {
            checkedMethods.add(checkProxy(method, bean));
        }
        MultiMethodRabbitListenerEndpoint endpoint = new MultiMethodRabbitListenerEndpoint(checkedMethods, bean);
        endpoint.setBeanFactory(this.beanFactory);
        processListener(endpoint,bean, bean.getClass(), beanName);
    }

    protected void processAmqpListener(Method method, Object bean,
            String beanName) {
        Method methodToUse = checkProxy(method, bean);
        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setMethod(methodToUse);
        endpoint.setBeanFactory(this.beanFactory);
        processListener(endpoint, bean, methodToUse, beanName);
    }

    private Method checkProxy(Method method, Object bean) {
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @RabbitListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException noMethod) {
                    }
                }
            } catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(
                        String.format(
                                "@RabbitListener method '%s' found on bean target class '%s', "
                                        + "but not found in any interface(s) for bean JDK proxy. Either "
                                        + "pull the method up to an interface or switch to subclass (CGLIB) "
                                        + "proxies by setting proxy-target-class/proxyTargetClass "
                                        + "attribute to 'true'", method.getName(), method
                                        .getDeclaringClass().getSimpleName()));
            }
        }
        return method;
    }

    protected void processListener(MethodRabbitListenerEndpoint endpoint, Object bean, Object adminTarget, String beanName) {
        endpoint.setBean(bean);
        endpoint.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory);
        endpoint.setId(getEndPointId());
        endpoint.setQueueNames(resolveQueue(bean));
        endpoint.setExclusive(false); //默认false
        //不设置handler的执行优先级
        //不设置admin，因为不使用auto-delete-queue.
        this.registrar.registerEndpoint(endpoint, null);
    }
    
    /**
     * 
     * @param object
     * @return String[]
     * @throws
     */
    private String[] resolveQueue(Object bean){
        Class<?> beanClass = AopUtils.getTargetClass(bean);
        EventCode taskCode = AnnotationUtils.findAnnotation(beanClass, EventCode.class);
        //检查注解
        if(taskCode == null || taskCode.value().length < 1){
            throw new IllegalArgumentException("请至少为listener分配一个可以执行的任务类型【"+beanClass.getSimpleName()+"】, see @EventCode annotation");
        }
        final List<String> queueNames = new ArrayList<String>();
        String[] taskCodes = taskCode.value();
        Stream.of(taskCodes).forEach(e->{
        	String queueName = EventQueueGenerator.gen(e);
        	mqRegistry.registQueue(queueName);
        	mqRegistry.registBindingWithName(queueName, Destinations.DEST_TANIQNG_FANOUT_EVENTBUS.getExchange(), Destinations.DEST_TANIQNG_FANOUT_EVENTBUS.getRoutingKey());
            queueNames.add(queueName);
        });
        return queueNames.toArray(new String[queueNames.size()]);
    }
    
    
    public String getEndPointId(){
        return "org.taniqng.eventbus.amqp.rabbit.RabbitListenerEndpointContainer#"
                + this.counter.getAndIncrement();
    }

    private class RabbitHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {

        private MessageHandlerMethodFactory messageHandlerMethodFactory;

        public void setMessageHandlerMethodFactory(
                MessageHandlerMethodFactory rabbitHandlerMethodFactory1) {
            this.messageHandlerMethodFactory = rabbitHandlerMethodFactory1;
        }

        @Override
        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
            return getMessageHandlerMethodFactory().createInvocableHandlerMethod(bean, method);
        }

        private MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
            if (this.messageHandlerMethodFactory == null) {
                this.messageHandlerMethodFactory = createDefaultMessageHandlerMethodFactory();
            }
            return this.messageHandlerMethodFactory;
        }

        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory defaultFactory =
                    new DefaultMessageHandlerMethodFactory();
            defaultFactory
                    .setBeanFactory(EventListenerPostProcessor.this.beanFactory);
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }

    }

}
