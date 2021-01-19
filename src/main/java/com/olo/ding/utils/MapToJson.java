package com.olo.ding.utils;

import java.util.Map;

import net.sf.json.JSONObject;

public class MapToJson {
	public static JSONObject mapToJson(Map<String, ?> map) {
		
		JSONObject obj = JSONObject.fromObject(map);
		return  obj;
	}
	
}
