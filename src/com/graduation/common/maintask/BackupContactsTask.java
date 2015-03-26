package com.graduation.common.maintask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class BackupContactsTask extends AsyncTask<Void, Integer, Boolean> {

	/**
	 * 加载本地通讯录的百分比
	 */
	public static final float LOCAL_READ_PERCENT = 30.0F;
	/**
	 * 服务器通讯录比较的百分比
	 */
	public static final float REMOTE_COMPARE_PERCENT = 40.0F;
	/**
	 * 本地和服务器新增比较的百分比
	 */
	public static final float COMPARE_ADD_END = 60.0F;
	/**
	 * 合并本地新增的百分比
	 */
	public static final float LOCAL_CONTACT_ADD_END = 70.0F;
	/**
	 * 插入本地数据库的百分比
	 */
	public static final float INSERT_DB_END = 90.0F;
	/**
	 * 版本重置的百分比
	 */
	public static final float READ_VERSION_END = 100.0F;
	private Handler mHandler;

	private  Context context;
	public BackupContactsTask( Context context,Handler mHandler) {
		this.context=context;
		this.mHandler = mHandler;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		// TODO Auto-generated method stub
		ContactsOperation operation = new ContactsOperation();
		operation.setHandler(mHandler);
		operation.backupContacts();
		return true;
	}

}
