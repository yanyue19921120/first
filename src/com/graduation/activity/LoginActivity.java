package com.graduation.activity;

import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.graduation.R;
import com.graduation.common.GraduationConst;
import com.graduation.common.MainApplication;
import com.graduation.common.widget.WaitDialog;
import com.graduation.common.widget.WaitDialog.OnCancleCallback;
import com.graduation.common.Login.LoginBody;
import com.graduation.common.Login.LoginResult;
import com.graduation.net.NetTask;
import com.graduation.net.NetTask.DataRecieved;
import com.graduation.phone.phoneUtils;

public class LoginActivity extends Activity {

	private Bitmap bmp;
	private WaitDialog waitDialog;
	// login asyn task
	private NetTask netTask;

	private Button buttonLogin = null;
	private EditText editUserName = null;
	private EditText editUserPassword = null;
	private String LoginUri = MainApplication.context
			.getString(R.string.app_server) + GraduationConst.LOGIN_PATH;
	private CheckBox checkBoxAutoLogin;
	private CheckBox checkBoxRememberPassword;
	private boolean autoLogin;
	private boolean rememberPassword;

	Context context;
	// 设置参数，防止多次点击登陆按钮
	private boolean isClickLoginButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context = this;
		ImageView iv = (ImageView) this.findViewById(R.id.login_bg_imageView);
		InputStream is = this.getResources().openRawResource(
				R.drawable.login_background);
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		bmp = BitmapFactory.decodeStream(is, null, opt);
		iv.setImageBitmap(bmp);

		isClickLoginButton = false;
		initControl();
		initControlState(this.getIntent().getExtras());

		checkBoxAutoLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkBoxAutoLogin.isChecked())
					checkBoxRememberPassword.setChecked(true);
			}
		});
		checkBoxRememberPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!checkBoxRememberPassword.isChecked()
						&& checkBoxAutoLogin.isChecked())
					checkBoxAutoLogin.setChecked(false);

			}
		});

		buttonLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = editUserName.getText().toString().trim();
				String password = editUserPassword.getText().toString().trim();
				if (username == null || username.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入用户名",
							Toast.LENGTH_SHORT).show();
				} else if (password == null || password.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else if (isClickLoginButton) {
					// already click login button ,but the task have not
					// finished
				} else if (!phoneUtils.isNetWorkAvaliable()) {
					Toast.makeText(getApplicationContext(), "当前网络不可用",
							Toast.LENGTH_SHORT).show();
				}

				else {

					if (MainApplication.phonenumber.equals("0")) {
						Toast.makeText(getApplicationContext(),
								"没有检测到SIM卡，部分功能将不可使用", Toast.LENGTH_SHORT)
								.show();
					}
					waitDialog = new WaitDialog(context, v);
					waitDialog.show();
					waitDialog.setOnCancleListener(new OnCancleCallback() {
						// if the network is very poor ,and user click cancle
						// before login success
						// and it's time to finish the asyntask
						@Override
						public void onCancled() {
							// TODO Auto-generated method stub
							// Toast.makeText(context, "dianjia qu xiao",
							// 1).show();
							if (!netTask.isCancelled() && netTask != null)
								netTask.cancel(true);
						}
					});
					LoginBody body = new LoginBody(username, password);
					String json = "";
					try {
						json = body.toJson();
					} catch (Exception e) {
						e.printStackTrace();
					}
					String[] params = { LoginUri, json };
					netTask = new NetTask();
					netTask.setDataRecieved(new DataRecieved() {
						@Override
						public void resultData(Object data) {
							onPostLogin((String) data);
						}
					});
					netTask.execute(params);
				}
			}

		});
	}

	

	private void initControlState(Bundle bundle) {
		// check which is the fore activity

		String lastActivity = bundle.getString("LastActivity");
		if (lastActivity.equals("SplashActivity")) {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			String userName = sp.getString("UserName", "");
			String passWord = sp.getString("PassWord", "");
			boolean rememberPsd = sp.getBoolean("RememberPassword", false);
			boolean autoLogin = sp.getBoolean("AutoLogin", false);

			editUserName.setText(userName);
			editUserPassword.setText(passWord);
			checkBoxAutoLogin.setChecked(autoLogin);
			checkBoxRememberPassword.setChecked(rememberPsd);

		} else if (lastActivity.equals("GuideActivity")) {
			// nothing to do
		} else if (lastActivity.equals("SettingAcitivity")) {

		}

	}

	private void getLoginState() {
		// TODO Auto-generated method stub
		autoLogin = checkBoxAutoLogin.isChecked();
		rememberPassword = checkBoxRememberPassword.isChecked();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK)
			if (!checkBoxRememberPassword.isChecked()) {
				SharedPreferences sp = getSharedPreferences("config",
						MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("UserName", null);
				editor.putString("PassWord", null);
				editor.commit();

			}
		return super.onKeyDown(keyCode, event);
	}

	private void onPostLogin(String result) {
		getLoginState();
		LoginResult loginResult = new LoginResult(result);
		if (loginResult.getResultCode() == 1) {
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			MainApplication.userid = loginResult.getUserid();
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean("AutoLogin", autoLogin);
			editor.putBoolean("RememberPassword", rememberPassword);
			if (!checkBoxRememberPassword.isChecked()) {
				editor.putString("UserName", null);
				editor.putString("PassWord", null);

			}
			if (autoLogin || rememberPassword) {
				editor.putString("UserName", editUserName.getText().toString()
						.trim());
				editor.putString("PassWord", editUserPassword.getText()
						.toString().trim());
			}
			editor.commit();
			if (waitDialog.isShowing())
				waitDialog.close();
			startActivity(intent);
			finish();
		} else {

			Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT).show();
			editUserPassword.setText(null);
			if (waitDialog.isShowing())
				waitDialog.close();
		}

	}

	private void initControl() {
		buttonLogin = (Button) this.findViewById(R.id.buttonLogin);
		editUserName = (EditText) this.findViewById(R.id.editTextUserName);
		editUserPassword = (EditText) this
				.findViewById(R.id.editTextUserPassword);
		checkBoxAutoLogin = (CheckBox) findViewById(R.id.checkBoxAutoLogin);
		checkBoxRememberPassword = (CheckBox) findViewById(R.id.checkBoxRemindPsd);
	}

}
