package com.olo.ding.utils;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;


public class JsonToMap {

	public static Map<String, Object> JsonToMap(String str) {
		return JSONObject.parseObject(str);
	}
}
