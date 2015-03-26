package com.graduation.json;

import java.util.Map;

public class Base {
	
	public String getStringFromMap(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		String value = null;
		if (obj != null) {
			value = obj.toString();
		}
		return value;
	}

	public int getIntFromMap(Map<String, Object> map, String key) {
		Object obj = map.get(key);
		int value = 0;
		if (obj instanceof Number) {
			value = ((Number) obj).intValue();
		} else if (obj instanceof String) {
			value = Integer.parseInt((String) obj, 16);
		}
		return value;
	}
	public boolean getBooleanFromMap(Map<String, Object> map, String key)
	{
		 Object val = map.get(key);
	        if (val != null) {
	            if (val instanceof String) {
	                return Integer.valueOf((String) val) == 1;
	            } else if (val instanceof Boolean) {
	                return ((Boolean) val).booleanValue();
	            } else if (val instanceof Number) {
	                return ((Number) val).intValue() == 1;
	            }
	        }
	        return true;
	}
}
