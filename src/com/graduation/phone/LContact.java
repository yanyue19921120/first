package com.graduation.phone;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.graduation.json.Base;

public class LContact extends Base {
	public boolean state;
	public int code;
	public String msg;
	public List<ContactBody> contactList;

	public LContact() {
		contactList=new ArrayList<ContactBody>();
	}

	public LContact(String jsonStr) throws JSONException {
		JSONObject jsonObject = new JSONObject(jsonStr);
		this.state = jsonObject.getBoolean("state");
		this.code = jsonObject.getInt("code");
		this.msg = jsonObject.getString("msg");
		contactList=new ArrayList<ContactBody>();
		if (jsonObject.get("contactList") instanceof JSONArray) {

			JSONArray jsonArray = (JSONArray) jsonObject.get("contactList");
			for (int i = 0; i < jsonArray.length(); i++) {
				ContactBody contactBody = new ContactBody();
				JSONObject obj = jsonArray.getJSONObject(i);
				contactBody.setCid(obj.getInt("cid"));
				contactBody.setRid(obj.getInt("rid"));
				contactBody.setAction(obj.getInt("action"));
				contactBody.setVcard(obj.getString("vcard"));
				contactList.add(contactBody);
			}
		}

	}

}
