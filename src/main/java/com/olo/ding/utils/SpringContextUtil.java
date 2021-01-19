package com.olo.ding.utils;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringContextUtil implements ApplicationContextAware{
	private static ApplicationContext context;
	 @Override
	 public void setApplicationContext(ApplicationContext contex)
	  throws BeansException
	 {
	  System.out.println("--------------------contex---------"+contex);
	  SpringContextUtil.context = contex;
	 }
	 public static ApplicationContext getApplicationContext() { 
	   return context; 
	 } 
	 public static Object getBean(String beanName) {
	  return context.getBean(beanName);
	 }
	 public static String getMessage(String key) {
	  return context.getMessage(key, null, Locale.getDefault());
	 }
}
