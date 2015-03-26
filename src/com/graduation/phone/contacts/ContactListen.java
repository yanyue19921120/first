package com.graduation.phone.contacts;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.provider.ContactsContract.RawContacts;

import com.graduation.common.MainApplication;

/**
 * This class listen the phone contacts database ... get the
 * changed,deleted,added contacts raw contacts id list
 * 
 * @author shenxy
 * 
 */
public class ContactListen {
	private static final String[] PHONES_PROJECTION = new String[] {
			RawContacts._ID, RawContacts.VERSION };
	public List<String> changedContacts = new ArrayList<String>();
	public List<String> deletedContacts = new ArrayList<String>();
	public List<String> addedContacts = new ArrayList<String>();

	/**
	 * Store the id and version . Call this method After update phone database
	 */
	public void saveIDAndVersion() {
		String id = "";
		String version = "";
		ContentResolver resolver = MainApplication.context.getContentResolver();
		Cursor phoneCursor = null;
		try {
			phoneCursor = resolver.query(RawContacts.CONTENT_URI,
					PHONES_PROJECTION, RawContacts.DELETED + " = 0 AND 1 = "
							+ RawContacts.DIRTY, null, null);
			if (phoneCursor != null) {
				while (phoneCursor.moveToNext()) {
					id += phoneCursor.getString(0) + "#";
					version += phoneCursor.getString(1) + "#";
				}
				SharedPreferences sp = MainApplication.context
						.getSharedPreferences("contact", 0);
				Editor editor = sp.edit();
				editor.putString("id", id);
				editor.putString("version", version);
				editor.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			phoneCursor.close();
		}
	}

	/**
	 * Construct method .
	 */
	public ContactListen() {
		changedContacts.clear();
		deletedContacts.clear();
		addedContacts.clear();
		String idStr;
		String versionStr;
		List<String> newid = new ArrayList<String>();
		List<String> newversion = new ArrayList<String>();
		SharedPreferences sp = MainApplication.context.getSharedPreferences(
				"contact", 0);
		idStr = sp.getString("id", "-1");
		if (idStr.equals("-1")) {
			saveIDAndVersion();
		} else {
			versionStr = sp.getString("version", "");
			String[] mid = idStr.split("#");
			String[] mversion = versionStr.split("#");
			ContentResolver resolver = MainApplication.context
					.getContentResolver();
			Cursor phoneCursor = null;
			try {
				phoneCursor = resolver.query(RawContacts.CONTENT_URI,
						PHONES_PROJECTION, RawContacts.DELETED
								+ " = 0 AND 1 = " + RawContacts.DIRTY, null,
						null);
				while (phoneCursor.moveToNext()) {
					newid.add(phoneCursor.getString(0));
					newversion.add(phoneCursor.getString(1));
				}
			} finally {
				phoneCursor.close();
			}
			for (int i = 0; i < mid.length; i++) {
				int k = newid.size();
				int j;
				for (j = 0; j <k; j++) {
					// if old id equals new id ,there is same contacts
					if (mid[i].equals(newid.get(j))) {
						if (!(mversion[i].equals(newversion.get(j)))) {
							changedContacts.add(newid.get(j));
							newid.remove(j);
							newversion.remove(j);
							break;
						}
						if (mversion[i].equals(newversion.get(j))) {
							newid.remove(j);
							newversion.remove(j);
							break;
						}
					}
				}
				if (j >= k) {
					deletedContacts.add(mid[i]);
				}
			}
			int n = newid.size();
			for (int m = 0; m < n; m++) {
				addedContacts.add(newid.get(m));
			}

		}

	}
}
