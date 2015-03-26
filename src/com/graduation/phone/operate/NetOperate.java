package com.graduation.phone.operate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import a_vcard.android.util.Log;

import com.graduation.R;
import com.graduation.common.GraduationConst;
import com.graduation.common.MainApplication;
import com.graduation.json.JsonUtils;
import com.graduation.net.NetUtils;
import com.graduation.phone.LContact;
import com.graduation.phone.contacts.ContactUtils.ContactStrInfoAndID;

public class NetOperate {
	public static LContact download() {
		String uri = MainApplication.context.getString(R.string.app_server)
				+ GraduationConst.DOWNLOAD_PATH;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", MainApplication.userid);
		params.put("imeicode", MainApplication.imeicode);
		LContact lContact = new LContact();
		try {
			String result = NetUtils.sendPost(uri, params, "utf-8");
			lContact = new LContact(result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lContact;
	}

	public static boolean upload(List<ContactStrInfoAndID> contactsInfoAndIDList) {
		boolean bRet = false;
		String uri = MainApplication.context.getString(R.string.app_server)
				+ GraduationConst.UPLOAD_PATH;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userid", MainApplication.userid);
		params.put("imeicode", MainApplication.imeicode);
		params.put("devicename", MainApplication.devicename);
		String contents = null;
		try {
			contents = JsonUtils.toJson(contactsInfoAndIDList);
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		params.put("contents", contents);
		try {
			//Log.v("parameters",params.toString());
			String result = NetUtils.sendPost(uri, params, "utf-8");
			Map<String, Object> map = JsonUtils.getMap(result);
			if((Integer)map.get("resultCode")==1)
			{
				bRet=true;
			}
		} catch (JSONException e) {
			//TODO
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.v("contents", contents);
		return bRet;
	}
}
