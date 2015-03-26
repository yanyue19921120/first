package com.graduation.common.Login;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.graduation.json.JsonUtils;

public class LoginResult {
	private int resultCode;
	private int userid;
	private String serverDeviceID;
	private String serverPhoneNumber;
	private String resultMessage;
	
	public LoginResult() {
		
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getServerDeviceID() {
		return serverDeviceID;
	}

	public void setServerDeviceID(String serverDeviceID) {
		this.serverDeviceID = serverDeviceID;
	}

	public String getServerPhoneNumber() {
		return serverPhoneNumber;
	}

	public void setServerPhoneNumber(String serverPhoneNumber) {
		this.serverPhoneNumber = serverPhoneNumber;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public LoginResult(String jsonString) {
		Map<String,Object> map=new HashMap<String, Object>();
		try {
			map=JsonUtils.getMap(jsonString);
			resultCode=Integer.valueOf(map.get("resultCode").toString());
			resultMessage=(String)map.get("resultMessage");
			userid=Integer.valueOf((String)map.get("userid").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


}
