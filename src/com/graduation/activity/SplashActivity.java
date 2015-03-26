package com.graduation.activity;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.ParseException;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.graduation.R;
import com.graduation.common.GraduationConst;
import com.graduation.common.MainApplication;
import com.graduation.common.Login.LoginBody;
import com.graduation.common.Login.LoginResult;
import com.graduation.net.NetUtils;
import com.graduation.phone.phoneUtils;

public class SplashActivity extends Activity {
	private Bitmap bmp;
	private boolean isFirstUse;
	private boolean isAutoLogin;
	private boolean isRememberPassword;
	private String userName;
	private String passWord;
	private Context context;
	private final Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		context = this;
		ImageView iv = (ImageView) this.findViewById(R.id.imageViewLoading);
		InputStream is = this.getResources().openRawResource(
				R.drawable.loading_background);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		bmp = BitmapFactory.decodeStream(is, null, opt);
		iv.setImageBitmap(bmp);

		new LoadingThread(this).start();
	}

	private void readPhoneInfo() {
		// TODO Auto-generated method stub
		// 1.获取手机基本信息：获取手机号 ，imeicode,devicename
		// 2，获取网络状态
		MainApplication.devicename = phoneUtils.getDeviceName();
		MainApplication.imeicode = phoneUtils.getIMEIcode();
		MainApplication.phonenumber = phoneUtils.getPhoneNumber();
	}

	private void readConfig() {
		// TODO Auto-generated method stub

		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		isFirstUse = sp.getBoolean("FirstUse", true);
		isAutoLogin = sp.getBoolean("AutoLogin", false);
		isRememberPassword = sp.getBoolean("RememberPassword", false);
		userName = sp.getString("UserName", "");
		passWord = sp.getString("PassWord", "");

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			// check whether it's the first time start this app

			startActivity(intent);
			SplashActivity.this.finish();
			if (!bmp.isRecycled())
				bmp.recycle();
			System.gc();

		}
	};

	private class LoadingThread extends Thread {
		private SplashActivity activity;

		public LoadingThread(SplashActivity spa) {
			this.activity = spa;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 处理事务
			// 1.获取手机基本信息：获取手机号 ，imeicode,devicename
			// 2，获取网络状态
			// 3.查看登陆参数
			readConfig();
			readPhoneInfo();
			try {
				sleep(1000);
				if (isFirstUse) {
					intent.setClass(SplashActivity.this, GuideActivity.class);
				} else if (!isFirstUse && isAutoLogin) {
					// 1.check the network ,if there is no available network,go
					// to
					// the login activity
					// 2.login faied ,go to login activity
					// 3.successful ,to the main activity
					// there is no need to start a wait dialog
					intent.putExtra("LastActivity", "SplashActivity");
					String LoginUri = MainApplication.context
							.getString(R.string.app_server)
							+ GraduationConst.LOGIN_PATH;
					if (!phoneUtils.isNetWorkAvaliable()) {
						// even though the network is unavailable ,we can pass a
						// flag to the login activity
						Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT)
								.show();
						intent.setClass(SplashActivity.this,
								LoginActivity.class);
					} else {
						LoginBody body = new LoginBody(userName, passWord);
						String json = "";
						try {

							json = body.toJson();
						} catch (Exception e) {
							// TODO Auto-generated catch block

							e.printStackTrace();
						}
						Object data = null;

						try {
							data = NetUtils.sendPost(LoginUri, json, "utf-8");
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

						LoginResult loginResult = new LoginResult((String) data);
						if (loginResult.getResultCode() == 1) {
							MainApplication.userid=loginResult.getUserid();
							intent.setClass(SplashActivity.this,
									MainActivity.class);
						} else {
							Toast.makeText(context, "自动登陆失败，请重新登陆",
									Toast.LENGTH_SHORT).show();
							intent.setClass(SplashActivity.this,
									LoginActivity.class);
						}
					}

				} else if (!isFirstUse && !isAutoLogin) {
					intent.putExtra("LastActivity", "SplashActivity");
					intent.setClass(SplashActivity.this, LoginActivity.class);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			activity.mHandler.sendEmptyMessage(0);
		}
	}

}
