package com.graduation.phone;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.graduation.phone.contacts.SMSListen;

public class ListenSMSService extends Service {

	private SMSListen smsObserver;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		 flags = START_STICKY;
		Uri SMS_INBOX = Uri.parse("content://sms/");
		Handler smsHandler = new Handler();
		smsObserver = new SMSListen(this, smsHandler);

		getContentResolver().registerContentObserver(SMS_INBOX, true,
				smsObserver);
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		getContentResolver().unregisterContentObserver(smsObserver);
		super.onDestroy();
	}

}
