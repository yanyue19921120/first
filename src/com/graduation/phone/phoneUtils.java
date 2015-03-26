package com.graduation.phone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.graduation.common.MainApplication;

/**
 * The utils can get the phone information
 * 
 * @author shenxy
 * 
 */
public class phoneUtils {

	private static TelephonyManager getTelephoneManager() {
		Context context = MainApplication.context;
		TelephonyManager mTelephonyManager;
		mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyManager;
	}

	/**
	 * get phone number
	 * 
	 * @return 0 if there is no SIM card
	 */
	public static String getPhoneNumber() {
		if (!isSIMCard()) {
			return String.valueOf(0);
		}
		TelephonyManager mTelephonyManager = getTelephoneManager();
		return mTelephonyManager.getLine1Number();
	}

	/**
	 * check whether the Network is avaliable
	 * 
	 * @return
	 */
	public static boolean isNetWorkAvaliable() {
		ConnectivityManager conn = (ConnectivityManager) MainApplication.context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net=conn.getActiveNetworkInfo();
		if(net!=null && net.isConnected())
		{
			return true;
		}
		return false;
		
	}
	/**
	 * 
	 */
	public static  void  startWifiSettingInterface(Context context)
	{
	   Intent intent =new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	context.startActivity(intent);
	}
	/** 
	 * check if there is A sim Card in the phone
	 * 
	 * @return
	 */
	private static boolean isSIMCard() {
		boolean bRet = false;
		TelephonyManager mTelephonyManager = getTelephoneManager();
		switch (mTelephonyManager.getSimState()) {
		case TelephonyManager.SIM_STATE_READY:
			bRet = true;
			break;
		}
		return bRet;
	}

	/**
	 * get the phone IMEI code . one IMEI code only can be used by one device
	 * 
	 * @return
	 */
	public static String getIMEIcode() {
		String imeiString = "";
		TelephonyManager mTelephonyManager = getTelephoneManager();
		imeiString = mTelephonyManager.getDeviceId();
		return imeiString;
	}

	@SuppressWarnings("static-access")
	public static String getDeviceName() {
		Build build = new Build();
		return build.MODEL;
	}

}
