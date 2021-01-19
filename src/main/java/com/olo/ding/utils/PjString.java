package com.olo.ding.utils;

import java.util.Map;

import org.apache.poi.ss.formula.functions.T;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class PjString {
	public static String pjString(Map<String, Object> map) {
		String body = JSONObject.toJSONString(map,SerializerFeature.WriteMapNullValue);
		return body;
	}
	public static String tojSONString(Map<String, Object> map) {
		return JSONObject.toJSONString(map,SerializerFeature.WriteMapNullValue);
	}
	
}
