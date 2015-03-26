package com.graduation.common.bean;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * This is a base class .
 * @author shenxy
 *
 */
public class JavaBean  {
	
	/**
	 * <p>This method can get it's child fields & parse it to a json string</p>
	 * this is a json demo:
	 * <p>{"password":"123","imeicode":"2874981510","phonenumber":"+861345803245","deviceName":"vovo txt","username":"admin"}</p>
	 * Note:all the fields are string
	 * @return
	 * @throws Exception
	 */
	public String toJson() throws Exception
	{
		String jsonString="{";
		String fieldName="";
		String fieldValue="";
		Class<? extends JavaBean> clazz=this.getClass();
		Field[] fields=clazz.getDeclaredFields();
		Field field;
		for(int i=0;i<fields.length;i++) {
			if(i!=0&&fields.length>0)
				jsonString+=",";
			field=fields[i];
			field.setAccessible(true);
			fieldName=field.getName();
			fieldValue=field.get(this).toString();
			jsonString+="\""+fieldName+"\":\""+fieldValue+"\"";
		}
		jsonString+="}";
		return jsonString;
	}
	/**
	 * parse a java bean to map
	 * @return A map
	 * @throws Exception
	 */
	public Map<String,String> toMap() throws Exception
	{
		Map<String,String> map=new HashMap<String, String>();
		Class<? extends JavaBean> clazz=this.getClass();
		Field[] fields=clazz.getDeclaredFields();
		for(Field field:fields) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(this).toString());
		}
		return map;
		
	}

}
