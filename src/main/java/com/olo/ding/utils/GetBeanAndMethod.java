package com.olo.ding.utils;

import java.lang.reflect.Method;
import java.util.Map;

public class GetBeanAndMethod {
	public static Method getMethod(String className,String methodName) {
		try {
			Class<?> clazz = Class.forName(className);
			Method method = clazz.getMethod(methodName);
			return method;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
			return null;
		}
	}
}
