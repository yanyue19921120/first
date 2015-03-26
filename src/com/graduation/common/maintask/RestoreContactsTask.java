package com.graduation.common.maintask;

import android.os.AsyncTask;
import android.os.Handler;

public class RestoreContactsTask extends AsyncTask<Void, Integer, Boolean> {

	private Handler mHandler;
	public RestoreContactsTask(Handler mHandler)
	{
		this.mHandler=mHandler;
	}
	@Override
	protected Boolean doInBackground(Void... params) {
		ContactsOperation operation=new ContactsOperation();
		operation.setHandler(mHandler);
		operation.restore();
		return true;
	}

}
