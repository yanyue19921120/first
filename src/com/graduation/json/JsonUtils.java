package com.graduation.json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {


	/**
	 * parse the Json string to Map
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	public static Map<String,Object> getMap(String jsonString) throws JSONException
	{
	    JSONObject jsonObject=new JSONObject(jsonString);
	    Map<String,Object> result=new HashMap<String,Object>();
	    String key=null;
	    Object value=null;
	    Iterator<?> iterator=jsonObject.keys();
	    while(iterator.hasNext()) {
	    	key=(String) iterator.next();
	    	value=jsonObject.get(key);
	    	result.put(key, value);
	    }
		return result;
	}
	
	@SuppressWarnings("null")
	public static <T> String toJson(List<T> objList) throws IllegalAccessException, IllegalArgumentException, JSONException
	{
		if(objList==null)
			return "";
		JSONArray jsonArray=new JSONArray();
		String fieldName="";
		String fieldValue="";
		for(Object obj:objList)
		{
			JSONObject object=new JSONObject();
			Class<? extends Object> clazz=obj.getClass();
			Field[] fields=clazz.getDeclaredFields();
			Field field;
			for(int i=0;i<fields.length;i++)
			{
				field=fields[i];
				field.setAccessible(true);
				fieldName=field.getName();
				fieldValue=field.get(obj).toString();
				//object=null;
				object.put(fieldName, fieldValue);
			}
			jsonArray.put(object);
		}
		return jsonArray.toString();
	}
//	public static Map<String,Object> json2Map(String jsonString)
//	{
//		Map<String,Object> map=new HashMap<String,Object>();
//		JSONObject json=JSONObject.fromObject(jsonString);
//		for(Object key:json.keySet()) {
//			Object value=json.get(key);
//			if(value instanceof JSONArray) {
//				List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
//				@SuppressWarnings("unchecked")
//				Iterator<JSONObject> it=((JSONArray)value).iterator();
//				while(it.hasNext()) {
//					JSONObject json2=it.next();
//					list.add(json2Map(json2.toString()));
//				}
//				map.put(key.toString(), list);
//			}else {
//				map.put(key.toString(), value);
//			}
//		}
//		return map;
//	}
	/**
	 * parse a map to json string
	 * @param map
	 * @return  json string
	 */
//	public static String map2Json(Map<String,Object> map)
//	{
//		String jsonString="";
//		jsonString=JSONObject.fromObject(map).toString();
//	    return jsonString;
//	}
	/**
	 * parse a list to json string
	 * @param list
	 * @return
	 */
//	public static String list2Json(List<String> list)
//	{
//		String jsonString="";
//		jsonString=JSONArray.fromObject(list).toString();
//		return jsonString;
//	}
//	public static String javabean2Json(Object o)
//	{
//		String jsonString="";
//        //jsonString =JSONObject.fromObject(o).toString();
//		return jsonString;
//		
//	}
//	
	
	

}
