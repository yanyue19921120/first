package com.graduation.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.ParseException;
import org.json.JSONException;

import a_vcard.android.util.Log;
import android.os.AsyncTask;

/**
 * <p>parameters[0] uri</p> 
 * parameters[1] json  string (post request)
 * @author shenxy
 * 
 */
public class NetTask extends AsyncTask<String, Integer, String> {

	
	@Override
	protected String doInBackground(String... params) {
		String uri=params[0];
		String jsonStrParams=params[1];
		String result="";
		try {
			result = NetUtils.sendPost(uri, jsonStrParams, "utf-8");
			Log.v("HEHE", jsonStrParams);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	DataRecieved dataRecieved;
	public  interface DataRecieved{
		public  void  resultData(Object data);
	}
	
	public void setDataRecieved(DataRecieved dataRecieved) {
		this.dataRecieved=dataRecieved;
	}
	@Override
	protected void onPostExecute(String result) {
		if(result!=null) {
			dataRecieved.resultData(result);
		}
		super.onPostExecute(result);
	}

}
