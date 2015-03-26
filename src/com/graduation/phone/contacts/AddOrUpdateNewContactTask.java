package com.graduation.phone.contacts;

import android.os.AsyncTask;

public class AddOrUpdateNewContactTask extends AsyncTask<ContactInfo, Integer, Boolean> {

	@Override
	protected Boolean doInBackground(ContactInfo... params) {
		// TODO Auto-generated method stub
		AddOrUpdateNewContactOperate operation=new AddOrUpdateNewContactOperate();
		return operation.execute(params[0]);
	}

}
