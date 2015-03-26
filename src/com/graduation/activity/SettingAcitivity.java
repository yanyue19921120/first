package com.graduation.activity;

import java.util.ArrayList;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.graduation.R;


public class SettingAcitivity extends Activity {
	private RelativeLayout backButton;
	private RelativeLayout openInfoButton;
	private RelativeLayout loginOutButton;
	//private CheckBox checkBoxListen;
	//private CheckBox checkBoxShowNotifaction;
	private SwitchButton switchbutton;
	private TextView title;
	private int state = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initControl();
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingAcitivity.this.finish();
			}
		});
		openInfoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts("package", getPackageName(), null);
				intent.setData(uri);
				startActivity(intent);
			}
		});
		loginOutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainActivity.instance.finish();
				Intent intent = new Intent(SettingAcitivity.this,
						LoginActivity.class);
				intent.putExtra("LastActivity", "SettingAcitivity");
				startActivity(intent);
				SettingAcitivity.this.finish();
				// TODO Auto-generated method stub
			}
		});
		/*checkBoxListen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkBoxListen.isChecked()) {
					Intent intent = new Intent(SettingAcitivity.this,
							ListenSMSService.class);
					startService(intent);
				}
				if (!checkBoxListen.isChecked()) {
					Intent intent = new Intent(SettingAcitivity.this,
							ListenSMSService.class);
					stopService(intent);
				}
			}
		});*/
		title = (TextView) findViewById(R.id.title);
		switchbutton = (SwitchButton) findViewById(R.id.switchbutton);
		switchbutton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean flag) {
				// TODO Auto-generated method stub
				if (flag) {
					title.setText("开");
				} else {
					title.setText("关");
				}
			}
		});

		if (state == 0) {
			switchbutton.setChecked(false);
			title.setText("关");
		}else{
			switchbutton.setChecked(true);
			title.setText("开");	
		}
	}
	

	private void initControl() {
		// TODO Auto-generated method stub
		if (isServiceRunning("ListenSMSService"))
			switchbutton.setChecked(true);
			//checkBoxListen.setChecked(true);
		else
			//checkBoxListen.setChecked(false);
			switchbutton.setChecked(false);

	}

	public boolean isServiceRunning(String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) activityManager
				.getRunningServices(100);
		for (int i = 0; i < runningService.size(); i++) {
			String classString = runningService.get(i).service.getClassName();
			if (classString.equals("com.graduation.phone.ListenSMSService")) {
				isRunning = true;
				break;
			}
		}

		return isRunning;

	}

	private void initView() {
		// TODO Auto-generated method stub

		backButton = (RelativeLayout) findViewById(R.id.relativelayout_back_button);
		openInfoButton = (RelativeLayout) findViewById(R.id.relativelayout_permission_setting_button);
		loginOutButton = (RelativeLayout) findViewById(R.id.relativelayout_login_out_button);
		//checkBoxListen = (CheckBox) findViewById(R.id.checkbox_listen);
		//checkBoxShowNotifaction = (CheckBox) findViewById(R.id.checkbox_show_notifaction);
		switchbutton = (SwitchButton) findViewById(R.id.switchbutton);
	}

}
