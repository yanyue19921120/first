package com.graduation.activity;

import a_vcard.android.util.Log;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graduation.R;
import com.graduation.common.GraduationConst;
import com.graduation.common.MainApplication;
import com.graduation.common.Login.GetRemoteCountAndDeviceBody;
import com.graduation.common.Login.GetRemoteCountAndDeviceResult;
import com.graduation.common.maintask.BackupContactsTask;
import com.graduation.common.maintask.RestoreContactsTask;
import com.graduation.common.other.CustomPopMenu;
import com.graduation.common.widget.circleProgressButton.CircleProgressButton;
import com.graduation.net.NetTask;
import com.graduation.net.NetTask.DataRecieved;
import com.graduation.phone.phoneUtils;
import com.graduation.phone.contacts.ContactUtils;

public class MainActivity extends Activity {

	public static MainActivity instance = null;
	// Main Menu instance
	private CustomPopMenu popMenu;
	private View menuView;

	// Main Menu
	private Button mainMenu;

	// sub Menu
	private LinearLayout subMenuRestore;
	private LinearLayout subMenuSendMsg;
	private LinearLayout subMenuMoreSetting;

	// button
	private CircleProgressButton backupButton;
	// TextView
	private TextView remoteCount_TextView;
	private TextView remoteDeviceName_TextView;
	private TextView localCount_TextView;
	private TextView localDeviceName_TextView;

	Context context;

	// Task get remote contacts count and remote device name
	String getCountDeviceUri = MainApplication.context
			.getString(R.string.app_server)
			+ GraduationConst.GET_REMOTE_COUNTS_DEVICENAME_PATH;
	private NetTask netTask;

	ImageView image;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GraduationConst.SYNC_CONTACTS:
				String percentString = msg.obj.toString();
				backupButton.setProgress(Float.valueOf(percentString)
						.intValue());
				backupButton.setProgressText(String.valueOf(Float.valueOf(
						percentString).intValue())
						+ "%");
				if (Float.valueOf(percentString).intValue() == 100)
				{
					//
					initRemote();
					initLocal();
				}
					break;
			case GraduationConst.SYNC_CONTACTS_ERROR:
				backupButton.setProgress(-1);
				break;
			case GraduationConst.RESTORE_CONTACTS:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instance = this;
		context = this;
		initControl();
		initRemote();
		initLocal();
		backupButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// // TODO Auto-generated method stub
				// if (backupButton.getProgress() == 0) {
				// simulateSuccessProgress(backupButton);
				// } else if (backupButton.getProgress() == 100) {
				// backupButton.setProgress(0);
				// } else {
				// backupButton.setProgress(-1);
				// }
				int k = backupButton.getProgress();
				Log.v("FF", String.valueOf(k));
				if (backupButton.getProgress() == 0) {
					backupContacts();
				} else if (backupButton.getProgress() == 100) {
					backupButton.setProgress(0);
				}

			}
		});
		mainMenu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popMenu != null) {
					if (popMenu.isShowing())
						popMenu.dismiss();
					else
						popMenu.showAsDropDown(v);
				}

			}
		});
		subMenuRestore.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (popMenu.isShowing())
					popMenu.dismiss();
				RestoreContacts();

			}
		});

		subMenuSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popMenu.isShowing())
					popMenu.dismiss();
				sendSMS("10086", "101");
			}
		});

		subMenuMoreSetting.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popMenu.isShowing())
					popMenu.dismiss();
				Intent intent = new Intent(MainActivity.this,
						SettingAcitivity.class);
				startActivity(intent);
			}
		});
		// buttonBackup.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Toast.makeText(MainActivity.this, "mmm", Toast.LENGTH_SHORT).show();
		// backupContacts();
		// }
		// });
	}

	private void initLocal() {
		// TODO Auto-generated method stub
		localCount_TextView.setVisibility(View.VISIBLE);
		localCount_TextView.setText(String.valueOf(ContactUtils
				.getContactsCount()));
	}

	private void initRemote() {

		GetRemoteCountAndDeviceBody body = new GetRemoteCountAndDeviceBody();
		String json = "";
		try {
			json = body.toJson();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		netTask = new NetTask();
		String params[] = { getCountDeviceUri, json };
		netTask.setDataRecieved(new DataRecieved() {

			@Override
			public void resultData(Object data) {
				// TODO Auto-generated method stub
				onPostRemoteData((String) data);
			}
		});
		netTask.execute(params);

	}

	protected void onPostRemoteData(String data) {
		// TODO Auto-generated method stub
		GetRemoteCountAndDeviceResult result = new GetRemoteCountAndDeviceResult(
				data);
		int resultCode = result.getResultCode();
		int remoteCount = result.getRemoteCount();
		String deviceName = result.getRemoteDeviceName();
		if (resultCode == 1) {
			if (remoteCount != 0) {
				remoteCount_TextView.setVisibility(View.VISIBLE);
				remoteCount_TextView.setText(String.valueOf(remoteCount));

				remoteDeviceName_TextView.setVisibility(View.VISIBLE);
				remoteDeviceName_TextView.setText(deviceName);
			} else {
				remoteCount_TextView.setVisibility(View.VISIBLE);
				remoteCount_TextView.setText("Î´±¸·Ý");
			}
		} else {
			// do nothing
		}
	}

	private void backupContacts() {
		BackupContactsTask task = new BackupContactsTask(MainActivity.this,
				mHandler);
		task.execute();
	}

	private void RestoreContacts() {
		RestoreContactsTask task = new RestoreContactsTask(mHandler);
		task.execute();

	}

	private void sendSMS(String phoneNumber, String msgText) {

		String SENT = "sms_sent";
		String DELIVERED = "sms_delivered";
		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Log.i("===>", "Activity.RESULT_OK");
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;

				}
			}
		}, new IntentFilter(SENT));
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Log.v("Hello", "RESULT_OK");
					Toast.makeText(context, "FASONG CHENGGONG", 1).show();
					;
					break;
				case Activity.RESULT_CANCELED:
					Log.i("===>", "RESULT_CANCLED");
					break;
				}
			}
		}, new IntentFilter(DELIVERED));
		SmsManager smsm = android.telephony.SmsManager.getDefault();
		smsm.sendTextMessage(phoneNumber, null, msgText, sentPI, deliveredPI);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initRemote();
		initLocal();
		if(backupButton.getProgress()==100||backupButton.getProgress()==-1)
			backupButton.setProgress(0);
		super.onResume();

	}

	private void simulateSuccessProgress(final CircleProgressButton button) {
		ValueAnimator widthAnimation = ValueAnimator.ofInt(0, 100);
		widthAnimation.setDuration(1500);
		widthAnimation.start();
		widthAnimation
				.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						Integer value = (Integer) animation.getAnimatedValue();
						button.setProgress(value);
						button.setProgressText(String.valueOf(value) + "%");
					}
				});
	}

	private void initControl() {
		popMenu = new CustomPopMenu(context, R.layout.popmenu_more_layout);
		menuView = popMenu.getView();
		mainMenu = (Button) this.findViewById(R.id.menu_button);
		subMenuRestore = (LinearLayout) menuView
				.findViewById(R.id.restore_contacts_linearlayout);
		subMenuSendMsg = (LinearLayout) menuView
				.findViewById(R.id.send_message_to_contacts_linearlayout);
		subMenuMoreSetting = (LinearLayout) menuView
				.findViewById(R.id.more_setting_linearlayout);
		backupButton = (CircleProgressButton) this
				.findViewById(R.id.circleButton_backup);
		localCount_TextView = (TextView) this
				.findViewById(R.id.local_contacts_number);
		localDeviceName_TextView = (TextView) findViewById(R.id.local_phone_name);
		remoteCount_TextView = (TextView) findViewById(R.id.cloud_contacts_number);
		remoteDeviceName_TextView = (TextView) findViewById(R.id.cloud_phone_name);

		localDeviceName_TextView.setVisibility(View.VISIBLE);
		localDeviceName_TextView.setText(MainApplication.devicename);

	}

}
