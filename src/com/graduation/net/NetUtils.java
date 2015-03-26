package com.graduation.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetUtils {
	/**
	 * send post to server
	 * 
	 * @param Uri
	 * @param params
	 * @param encoding
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String sendPost(String Uri, Map<String, Object> params,
			String encoding) throws ClientProtocolException, IOException {
		String result = "";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Uri);
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				String name = entry.getKey();
				String value = entry.getValue().toString();
				BasicNameValuePair valuePair = new BasicNameValuePair(name,
						value);
				parameters.add(valuePair);
			}
		}
		UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
				parameters, encoding);
		httpPost.setEntity(encodedFormEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), encoding);
		}
		return result;
	}

	/**
	 * send post to server
	 * 
	 * @param Uri
	 * @param jsonString
	 * @param encoding
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String sendPost(String Uri, String jsonString, String encoding)
			throws UnsupportedEncodingException, JSONException, ParseException,
			IOException {
		String result = "";
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		JSONObject jsonObject = new JSONObject(jsonString);
		JSONArray jsonArray = jsonObject.names();
		for (int i = 0; i < jsonArray.length(); i++) {
			String name = jsonArray.getString(i).toString();
			String value = jsonObject.getString(name);
			BasicNameValuePair valuePair = new BasicNameValuePair(name, value);
			parameters.add(valuePair);
		}
		UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(
				parameters, encoding);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(Uri);
		httpPost.setEntity(encodedFormEntity);
		HttpResponse httpResponse = httpClient.execute(httpPost);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			result = EntityUtils.toString(httpResponse.getEntity(), encoding);
		}
		return result;

	}
}
