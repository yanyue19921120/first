package com.graduation.common;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application {
	public static Context context;
	public static String imeicode;
	public static String phonenumber;
	public static String devicename;
	public static int userid;

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();

	}

}
