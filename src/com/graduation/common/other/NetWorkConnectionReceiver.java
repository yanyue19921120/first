package com.graduation.common.other;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.SurfaceHolder.BadSurfaceTypeException;

public class NetWorkConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			//网络状态发生改变
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetworkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetworkInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if(!mobNetworkInfo.isConnected()&&!wifiNetworkInfo.isConnected()) {
			//wangluo bu keyong
		}
		else {
			
		}
		
	}

}
