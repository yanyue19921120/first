package com.graduation.common.Login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.graduation.json.JsonUtils;

public class GetRemoteCountAndDeviceResult {

	private int remoteCount;
	private int resultCode;
	private String remoteDeviceName;

	public GetRemoteCountAndDeviceResult() {
		// TODO Auto-generated constructor stub
	}

	public int getRemoteCount() {
		return remoteCount;
	}

	public void setRemoteCount(int remoteCount) {
		this.remoteCount = remoteCount;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getRemoteDeviceName() {
		return remoteDeviceName;
	}

	public void setRemoteDeviceName(String remoteDeviceName) {
		this.remoteDeviceName = remoteDeviceName;
	}

	public GetRemoteCountAndDeviceResult(String resultJson) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = JsonUtils.getMap(resultJson);
			resultCode = (Integer) map.get("resultCode");
			remoteCount = (Integer) map.get("remoteContactsCount");
			remoteDeviceName = (String) map.get("remoteDeviceName");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
