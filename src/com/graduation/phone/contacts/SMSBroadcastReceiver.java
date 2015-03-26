package com.graduation.phone.contacts;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		SmsMessage msg=null;
		Bundle bundle=intent.getExtras();
		if(bundle!=null)
		{
			Object[] pdusObj=(Object[]) bundle.get("pdus");
			for(Object p:pdusObj)
			{
				msg=SmsMessage.createFromPdu((byte[]) p);
				String msgText=msg.getMessageBody();
				Date date=new Date(msg.getTimestampMillis());
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String receiveTime=format.format(date);
				String senderNumber=msg.getOriginatingAddress();
				Toast.makeText(context, msgText+"sender"+senderNumber, 1).show();
			}
		}
	}

}
