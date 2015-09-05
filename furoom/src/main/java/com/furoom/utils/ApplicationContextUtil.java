package com.furoom.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
@Component
public class ApplicationContextUtil implements ApplicationContextAware{
	private static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext=applicationContext;
	}
	public static <T> T getBean(Class<T> requiredType)
	{
		return applicationContext.getBean(requiredType);
	}
	public static <T> T getBean(String name,Class<T> requiredType)
	{
		if(name==null)
			return getBean(requiredType);
		return applicationContext.getBean(name,requiredType);
	}

}
