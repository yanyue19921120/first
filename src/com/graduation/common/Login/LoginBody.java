package com.graduation.common.Login;
import com.graduation.common.MainApplication;
import com.graduation.common.bean.JavaBean;

/**
 * java bean .Login struct
 * @author shenxy
 *
 */
public class LoginBody extends JavaBean{
	private String username;
	private String password;
	private String imeicode;
	private String phonenumber;
	private String deviceName;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getImeicode() {
		return imeicode;
	}

	public void setImeicode(String imeicode) {
		this.imeicode = imeicode;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public LoginBody() {

	}

	public LoginBody(String username, String password) {
		this.username = username;
		this.password = password;
		this.deviceName = MainApplication.devicename;
		this.imeicode = MainApplication.imeicode;
		this.phonenumber = MainApplication.phonenumber;
	}
	
	

}
