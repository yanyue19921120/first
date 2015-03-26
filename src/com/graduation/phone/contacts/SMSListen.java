package com.graduation.phone.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.graduation.common.other.NewPhoneNumber;
import com.graduation.phone.contacts.ContactInfo.PhoneInfo;
import com.graduation.phone.contacts.ContactUtils.ContactStrInfoAndID;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class SMSListen extends ContentObserver {

	private Context context;

	public SMSListen(Context context, Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		getSmsFromPhone(context);
	}

	private NewPhoneNumber isNewContact(NumberAndMessage numberAndMessage) {
		NewPhoneNumber newPhoneNumber = new NewPhoneNumber();
		ContactInfo contactInfo = new ContactInfo();
		List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
		ContactInfo.PhoneInfo oneInfo = new PhoneInfo();
		// xxx,我是xxx,这是我的新号码，xxxxxx
		String partternOne = ".*我[是]?(.+)[,，;；\\s[.]。]+.*新号码.*";
		// xxx,这是我的新号码，我是xxx
		String partternTwo = ".*新号码[,，;；\\s[.]。]+我是(.+)[,，;；\\s[.]。]+.*";
		// 我是xxx.我换号了
		String partternThree = "我[是]?(.+)[,，;；\\s[.]。]+.*换号[码]?.*";
		// 姓名:xxxx 电话号码：13234567897
		String patternFour = "姓名[:：；;\\s,，.。]?[\\s]*(.+)[\\s]*电话号码[:：；;\\s,，.。]?[\\s]*.*(1[0-9]{10}).*";
		// Pattern pattern = Pattern.compile(patternStr);
		// Matcher matcher = pattern.matcher(message);
		List<String> patternString = new ArrayList<String>();
		patternString.add(partternOne);
		patternString.add(partternTwo);
		patternString.add(partternThree);
		patternString.add(patternFour);

		for (String patternStr : patternString) {

			Pattern pattern = Pattern.compile(patternStr);
			Matcher matcher = pattern.matcher(numberAndMessage.Message);
			if (patternStr.equals(patternFour)) {
				if (matcher.find()) {
					newPhoneNumber.setNewNumber(true);
					contactInfo.setName(matcher.group(1));
					oneInfo.type = 2;
					oneInfo.number = matcher.group(2);
					phoneInfoList.add(oneInfo);
					contactInfo.setPhoneList(phoneInfoList);
					newPhoneNumber.setContactInfo(contactInfo);
					break;
				}
			} else if (patternStr.equals(partternOne)
					|| patternStr.equals(partternTwo)
					|| patternStr.equals(partternThree)) {
				if (matcher.find()) {
					newPhoneNumber.setNewNumber(true);
					contactInfo.setName(matcher.group(1));
					oneInfo.type = 2;
					oneInfo.number = numberAndMessage.number;
					phoneInfoList.add(oneInfo);
					contactInfo.setPhoneList(phoneInfoList);
					newPhoneNumber.setContactInfo(contactInfo);
					break;
				}
			}

		}

		return newPhoneNumber;
	}

	private class NumberAndMessage {
		public String number;
		public String Message;

		public NumberAndMessage() {
			number = "";
			Message = "";
		}
	}

	private void getSmsFromPhone(Context context) {
		// TODO Auto-generated method stub
		Uri SMS_INBOX = Uri.parse("content://sms/inbox");
		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "_id", "address", "body" };
		String where = " read=0 and  status=-1 and   type=1";
		Cursor cur = null;
		cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		NumberAndMessage numberAndMessage = new NumberAndMessage();
		if (null == cur || cur.getCount() == 0)
			return;
		else {
			String number = "";
			String body = "";
			String id = "";
			if (cur.moveToNext()) {

				id = cur.getString(cur.getColumnIndex("_id"));
				number = cur.getString(cur.getColumnIndex("address"));
				body = cur.getString(cur.getColumnIndex("body"));

			}
			// Toast.makeText(context, number+body, 1).show();
			numberAndMessage.number = number;
			numberAndMessage.Message = body;
		}
		NewPhoneNumber newPhoneNumber = isNewContact(numberAndMessage);
		if (newPhoneNumber.isNewNumber()) {
			
			ContentValues values = new ContentValues();
			values.put("read", "1");
			context.getContentResolver().update(
					Uri.parse("content://sms/inbox"), values, " _id=?",
					new String[] { "" + cur.getInt(0) });
			
			AddOrUpdateNewContactTask task=new AddOrUpdateNewContactTask();
			ContactInfo [] params= {newPhoneNumber.getContactInfo()};
			task.execute(params);
		}

	}
}
