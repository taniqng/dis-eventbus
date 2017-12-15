package com.github.taniqng.eventbus.core;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * 托管给spring的properties属性读取工具
 */
@Component
public class PropertiesLoaderForBus implements BeanFactoryPostProcessor {
  
  private static PropertiesLoaderForBus loader;
  {
    PropertiesLoaderForBus.loader = this;
  }
  
  private ConfigurableListableBeanFactory beanFactory;
  
  private static final Map<String, String> cache = new ConcurrentHashMap<String, String>(128,0.8f,4);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    this.beanFactory = beanFactory;
  }
  
  public static String getProperty(String key){
    return loader._getProperty(key);
  }
  
  public static String getPropViaExp(String exp){
    return loader._getPropViaExp(exp);
  }
  
  public static String getProperty(String key, String valIfNull){
    return Optional.ofNullable(loader._getProperty(key)).orElse(valIfNull);
  }
  
  public static Integer getInteger(String key){
    return loader._getInteger(key);
  }
  
  /**
   * 获取指定property
   * @param key
   * @return
   */
  private String _getProperty(String key){
    return _getPropViaExp("${"+key+"}");
  }
  
  private String _getPropViaExp(String exp){
    if(exp == null) return null;
    try{
      String val = Optional.ofNullable(cache.get(exp)).orElseGet(()-> beanFactory.resolveEmbeddedValue(exp));
      //这里判断这个值是否已经被其他线程先一步缓存，如果缓存了，忽略此处取值
      String oval = cache.putIfAbsent(exp, val);
      //这里可以放心的使用了，所有值都来自于缓存
      return oval != null? oval: val;
    } catch (Throwable e){
      return null;
    }
  }
  
  /**
   * 获取指定的Integer
   * @param key
   * @return
   */
  private Integer _getInteger(String key){
    if(key == null) return null;
    return Optional.ofNullable(_getProperty(key)).map(e->Integer.parseInt(e)).get();
  }
  
}