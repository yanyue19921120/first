package com.graduation.common.Login;

import com.graduation.common.MainApplication;
import com.graduation.common.bean.JavaBean;

public class GetRemoteCountAndDeviceBody extends JavaBean {
	private int userid;

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public GetRemoteCountAndDeviceBody() {
		this.userid = MainApplication.userid;
	}

}
