package com.graduation.phone.contacts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.graduation.common.MainApplication;
import com.graduation.common.maintask.BackupContactsTask;
import com.graduation.common.maintask.ContactsOperation;
import com.graduation.phone.ContactBody;

/**
 * 
 * The utils of handle contact information
 * 
 * @author shenxy
 * 
 */
public class ContactUtils {

	/**
	 *  1.Contacts vcard string 2.Contacts ID(Integer) in
	 * every phone 3.Contacts remote contact id(Integer) if remote don't have
	 * this contacts the rid would be 0(default) 4.action: The action of a
	 * contact the contacts had been deleted (action=1) otherwise it always be 0
	 * 
	 * @author shenxy
	 * 
	 */
	public static class ContactStrInfoAndID {
		public String vcard;
		public int cid;
		public int rid = 0;
		public int action = 0;
		public ContactStrInfoAndID(ContactBody contactBody)
		{
		   this.action=contactBody.getAction();
		   this.cid=contactBody.getCid();
		   this.rid=contactBody.getRid();
		   this.vcard=contactBody.getVcard();
		}
		public ContactStrInfoAndID()
		{
			
		}
	}
	public static class ContactInfoAndID {
		public ContactInfo contactInfo;
		public int cid;
		public ContactInfoAndID()
		{
			contactInfo=new ContactInfo();
		}
		public ContactInfoAndID(ContactInfo contactInfo)
		{
			this.contactInfo=contactInfo;
		}
	}

	/**
	 * Parse the vcard String to Contacts
	 * 
	 * @param vcardstring
	 * @return a ContactInfo class object
	 * @throws Exception
	 *             can't parse
	 */
	public static ContactInfo getContact(String vcardstring) throws Exception {
		ContactInfo oneContact = new ContactInfo();
		VCardParser cardparse = new VCardParser();
		VDataBuilder builder = new VDataBuilder();
		boolean isParse = cardparse.parse(vcardstring, "UTF-8", builder);
		if (!isParse) {
			throw new Exception("Can't parase the vCard,please check the vCard");
		}
		List<VNode> pimContacts = builder.vNodeList;
		VNode contact = pimContacts.get(0);
		ContactStruct contactStruct = ContactStruct.constructContactFromVNode(
				contact, 1);
		List<PhoneData> phoneDataList = contactStruct.phoneList;
		List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
		if (phoneDataList != null) {
			for (PhoneData phoneData : phoneDataList) {
				ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
				phoneInfo.type = phoneData.type;
				phoneInfo.number = phoneData.data;
				phoneInfoList.add(phoneInfo);
			}
		}
		oneContact.setPhoneList(phoneInfoList);
		oneContact.setName(contactStruct.name);
		return oneContact;
	}

	/**
	 * Parse contacts to vcard string
	 * 
	 * @param info
	 * @return vcard string
	 */
	public static String Contact2String(ContactInfo info) {
		String vcardstring = "";
		VCardComposer composer = new VCardComposer();
		ContactStruct contact = new ContactStruct();
		contact.name = info.getName();
		List<ContactInfo.PhoneInfo> numberList = info.getPhoneList();
		for (ContactInfo.PhoneInfo phoneInfo : numberList) {
			String label = "";
			contact.addPhone(phoneInfo.type, phoneInfo.number, label, true);
		}
		try {
			vcardstring = composer.createVCard(contact,
					VCardComposer.VERSION_VCARD30_INT);
		} catch (VCardException e) {
			e.printStackTrace();
		}
		return vcardstring;
	}

	public static int getContactsCount()
	{
		Context context=MainApplication.context;
		int count=-1;
		Cursor cur=null;
		try {
		cur = context.getContentResolver().query(
				RawContacts.CONTENT_URI,
				null,
				ContactsContract.Data.CONTACT_ID + "> ? AND "
						+ RawContacts.DELETED + " =0 AND "
						+ RawContacts.DIRTY + " =1 ",
				new String[] { "0", },
				null);
		count=cur.getCount();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return count;
		
	}
	/**
	 * Query contacts information from phone database
	 * 
	 * @return A hashmap (key(Contact ID), value(ContactStrInfoAndID object))
	 */
	public static LinkedHashMap<Integer, ContactStrInfoAndID> getPhoneContactsAndID(ContactsOperation contactsOperation,boolean isSyn) {
		Context context = MainApplication.context;
		LinkedHashMap<Integer, ContactStrInfoAndID> linkedMap = new LinkedHashMap<Integer, ContactStrInfoAndID>();
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(
					RawContacts.CONTENT_URI,
					null,
					ContactsContract.Data.CONTACT_ID + "> ? AND "
							+ RawContacts.DELETED + " =0 AND "
							+ RawContacts.DIRTY + " =1 ",
					new String[] { "0", },
					ContactsContract.Contacts._ID + " COLLATE LOCALIZED ASC");

			    long read_size = 0;
	            float progressVal = 0;
	            long contact_size = cur.getCount();
	            // 根据当前通讯录的数据量产生一个可能让进度平滑一些比较刻度值，根据该刻度值产生进度
	            long rule_size = contact_size > 1000 ? 20 : contact_size > 500 ? 10 : contact_size > 100 ? 5 : 1;
			if (cur.moveToFirst()) {
				do {
					if (read_size % rule_size == 0) {
                        progressVal = Float.valueOf(read_size * BackupContactsTask.LOCAL_READ_PERCENT / contact_size);
                        if(isSyn)
                        	contactsOperation.sendSynProgress(progressVal);
                        else
                        	contactsOperation.sendRestoreProgress(progressVal);
                    }
					ContactStrInfoAndID oneContact = new ContactStrInfoAndID();
					ContactInfo info = new ContactInfo();
					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.RawContacts._ID));
					String displayName = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					oneContact.cid = Integer.valueOf(id);
					String contact_id = cur
							.getString(cur
									.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
					info.setName(displayName);
					Cursor phonesCursor = null;
					try {
						phonesCursor = context
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ "=" + contact_id,
										null,
										ContactsContract.CommonDataKinds.Phone.NUMBER
												+ " ASC");
						if (phonesCursor.moveToFirst()) {
							List<ContactInfo.PhoneInfo> phoneNumberList = new ArrayList<ContactInfo.PhoneInfo>();
							do {
								String phoneNumber = phonesCursor
										.getString(phonesCursor
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //
								int type = phonesCursor
										.getInt(phonesCursor
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
								ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
								phoneInfo.type = type;
								phoneInfo.number = phoneNumber;
								phoneNumberList.add(phoneInfo);
							} while (phonesCursor.moveToNext());
							info.setPhoneList(phoneNumberList);
						}
					} finally {
						// force to close the cursor
						phonesCursor.close();
					}
					oneContact.vcard = info.toString();
					linkedMap.put(Integer.valueOf(id), oneContact);
					read_size++;
				} while (cur.moveToNext());
			}
		} finally {
			// force to close the cursor
			cur.close();
		}
		return linkedMap;
	}
	
	public static LinkedHashMap<Integer, ContactInfoAndID> getPhoneContacts() {
		Context context = MainApplication.context;
		LinkedHashMap<Integer, ContactInfoAndID> linkedMap = new LinkedHashMap<Integer, ContactInfoAndID>();
		Cursor cur = null;
		try {
			cur = context.getContentResolver().query(
					RawContacts.CONTENT_URI,
					null,
					ContactsContract.Data.CONTACT_ID + "> ? AND "
							+ RawContacts.DELETED + " =0 AND "
							+ RawContacts.DIRTY + " =1 ",
					new String[] { "0", },
					ContactsContract.Contacts._ID + " COLLATE LOCALIZED ASC");

			if (cur.moveToFirst()) {
				do {
					ContactInfoAndID oneContact = new ContactInfoAndID();
					ContactInfo info = new ContactInfo();
					String id = cur.getString(cur
							.getColumnIndex(ContactsContract.RawContacts._ID));
					String displayName = cur
							.getString(cur
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					oneContact.cid = Integer.valueOf(id);
					String contact_id = cur
							.getString(cur
									.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
					info.setName(displayName);
					Cursor phonesCursor = null;
					try {
						phonesCursor = context
								.getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
												+ "=" + contact_id,
										null,
										ContactsContract.CommonDataKinds.Phone.NUMBER
												+ " ASC");
						if (phonesCursor.moveToFirst()) {
							List<ContactInfo.PhoneInfo> phoneNumberList = new ArrayList<ContactInfo.PhoneInfo>();
							do {
								String phoneNumber = phonesCursor
										.getString(phonesCursor
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //
								int type = phonesCursor
										.getInt(phonesCursor
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
								ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
								phoneInfo.type = type;
								phoneInfo.number = phoneNumber;
								phoneNumberList.add(phoneInfo);
							} while (phonesCursor.moveToNext());
							info.setPhoneList(phoneNumberList);
						}
					} finally {
						// force to close the cursor
						phonesCursor.close();
					}
					oneContact.contactInfo=info;
					linkedMap.put(Integer.valueOf(id), oneContact);
				} while (cur.moveToNext());
			}
		} finally {
			// force to close the cursor
			cur.close();
		}
		return linkedMap;
	}

	/**
	 * add one contact to buffer area
	 * 
	 * @param ops
	 * @param infoAndID
	 */
	private static void addNewContacts(ArrayList<ContentProviderOperation> ops,
			ContactStrInfoAndID infoAndID) {

		int rawContactInsertIndex = ops.size();
		// First insert a null value to ops
		Builder builder = ContentProviderOperation
				.newInsert(ContactsContract.RawContacts.CONTENT_URI);
		builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null);
		builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
		builder.withValue(ContactsContract.RawContacts.AGGREGATION_MODE,
				ContactsContract.RawContacts.AGGREGATION_MODE_DISABLED);
		ops.add(builder.build());
		ContactInfo info = new ContactInfo();
		try {
			info = ContactUtils.getContact(infoAndID.vcard);
		} catch (Exception e) {
			e.printStackTrace();
		}

		builder = ContentProviderOperation
				.newInsert(android.provider.ContactsContract.Data.CONTENT_URI);
		builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,
				rawContactInsertIndex);
		builder.withValue(
				ContactsContract.Data.MIMETYPE,
				ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
		// insert the contacts name to ops
		builder.withValue(
				ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				info.getName());
		ops.add(builder.build());

		List<ContactInfo.PhoneInfo> phoneList = info.getPhoneList();
		// insert the phone list to ops
		for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
			builder = ContentProviderOperation
					.newInsert(android.provider.ContactsContract.Data.CONTENT_URI);
			builder.withValueBackReference(
					ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex);
			builder.withValue(ContactsContract.Data.MIMETYPE,
					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
			builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,
					phoneInfo.number);
			builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
					phoneInfo.type);
			ops.add(builder.build());
		}
	}

	/**
	 * A batch insert to phone DB
	 * 
	 * @param ops
	 *            The operation
	 * @param contactsInfo
	 * @param index
	 * @throws Exception
	 */
	private static void applyBatchAdd(ArrayList<ContentProviderOperation> ops,
			List<ContactStrInfoAndID> contactsInfo, int index) throws Exception {
		Context context = MainApplication.context;
		ContentProviderResult[] results;
		results = context.getContentResolver().applyBatch(
				ContactsContract.AUTHORITY, ops);
		for (ContentProviderResult result : results) {
			if (result.uri == null) {
				index++;
				continue;
			}
			if (result.uri.toString().startsWith(
					ContactsContract.RawContacts.CONTENT_URI.toString())) {
				long rawID = ContentUris.parseId(result.uri);
				contactsInfo.get(index).cid = Integer.valueOf(String
						.valueOf(rawID));
			}
		}
	}

	/**
	 * batch insert contacts information to phone database this method will
	 * return raw contacts id to the ContactStrInfoAndID object
	 * 
	 * @param contactsInfo
	 */
	public static void insertContactToDB(List<ContactStrInfoAndID> contactsInfo) {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		int index = 1;
		int offset = 0;
		for (ContactStrInfoAndID infoAndID : contactsInfo) {
			addNewContacts(ops, infoAndID);
			// if the insert number greater than 100,apply batch insert 100 data
			// at once.
			// if the amount of the insert are greater than 100 at once,this
			// operation may cause error
			if (index > 0 && index % 100 == 0) {
				try {
					applyBatchAdd(ops, contactsInfo, offset);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ops.clear();
				offset += 100;
			}
			index++;
		}
		if (ops.size() > 0) {
			try {
				applyBatchAdd(ops, contactsInfo, offset);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * delete contacts information from phone database
	 * 
	 * @param contactsID
	 */
	public static void deleteContactsDB(List<Integer> contactsID) {
		Context context = MainApplication.context;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		for (int contactID : contactsID) {
			ops.add(ContentProviderOperation
					.newDelete(ContactsContract.RawContacts.CONTENT_URI)
					.withSelection(Data._ID + " = ? ",
							new String[] { String.valueOf(contactID) }).build());
			if (ops.size() >= 100) {
				try {
					ContentProviderResult[] results = context
							.getContentResolver().applyBatch(
									ContactsContract.AUTHORITY, ops);
					for (ContentProviderResult result : results) {
						if (result.uri == null) {
							Log.d("deleteContactsDB", "delete Error");
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
				ops.clear();
			}
		}
		if (ops.size() > 0) {
			try {
				ContentProviderResult[] results = context.getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
				for (ContentProviderResult result : results) {
					if (result.uri == null) {
						Log.d("deleteContactsDB", "delete Error");
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * add ContactStrInfoAndID to the operation list
	 * 
	 * @param contact
	 * @param ops
	 */
	private static void addUpdateContacts(ContactStrInfoAndID contact,
			ArrayList<ContentProviderOperation> ops) {
		int rawID = contact.cid;
		ContactInfo info = new ContactInfo();
		try {
			info = getContact(contact.vcard);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		// first delete the exist,then insert
		// delete
		//
		Builder builder = ContentProviderOperation
				.newDelete(android.provider.ContactsContract.Data.CONTENT_URI);
		builder.withSelection(
				ContactsContract.Data.RAW_CONTACT_ID + "=? AND "
						+ ContactsContract.Data.MIMETYPE + "!=? AND "
						+ ContactsContract.Data.MIMETYPE + "!=?",
				new String[] {
						String.valueOf(rawID),
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
						ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE });
		ops.add(builder.build());

		ContentValues values;
		// insert
		List<ContactInfo.PhoneInfo> phones = info.getPhoneList();
		for (ContactInfo.PhoneInfo phoneInfo : phones) {
			values = new ContentValues();
			values.put(ContactsContract.Data.RAW_CONTACT_ID, rawID);
			values.put(ContactsContract.Data.DATA1, phoneInfo.number);
			values.put(ContactsContract.Data.DATA2, phoneInfo.type);
			values.put(ContactsContract.Data.MIMETYPE,
					ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
			builder = ContentProviderOperation.newInsert(
					ContactsContract.Data.CONTENT_URI).withValues(values);
			ops.add(builder.build());
		}
		//
		// just update
		//
		builder = ContentProviderOperation
				.newUpdate(android.provider.ContactsContract.Data.CONTENT_URI);
		builder.withSelection(
				ContactsContract.Data.RAW_CONTACT_ID + "=?" + " AND "
						+ ContactsContract.Data.MIMETYPE + "=?",
				new String[] {
						String.valueOf(rawID),
						ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE });
		builder.withValue(
				ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				info.getName());
		ops.add(builder.build());
	}

	/**
	 * Update the phone contacts information
	 * 
	 * @param contactStrInfoAndID
	 */
	public static void updateContactDB(
			List<ContactStrInfoAndID> contactStrInfoAndID) {
		Context context = MainApplication.context;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		for (int i = 0; i < contactStrInfoAndID.size(); i++) {
			ContactStrInfoAndID infoAndID = contactStrInfoAndID.get(i);
			addUpdateContacts(infoAndID, ops);
			if (i > 0 && i % 100 == 0) {
				try {
					ContentProviderResult[] results = context
							.getContentResolver().applyBatch(
									ContactsContract.AUTHORITY, ops);
					for (ContentProviderResult result : results) {
						if (result.uri == null) {
							continue;
						}
						if (result.uri.toString().startsWith(
								ContactsContract.RawContacts.CONTENT_URI
										.toString())) {
							long rawID = ContentUris.parseId(result.uri);
							Log.d("updateContactDB", "Apply batch rawID"
									+ rawID);
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					e.printStackTrace();
				}
				ops.clear();
			}
		}
		if (ops.size() > 0) {
			try {
				ContentProviderResult[] results = context.getContentResolver()
						.applyBatch(ContactsContract.AUTHORITY, ops);
				for (ContentProviderResult result : results) {
					if (result.uri == null) {
						continue;
					}
					if (result.uri.toString()
							.startsWith(
									ContactsContract.RawContacts.CONTENT_URI
											.toString())) {
						long rawID = ContentUris.parseId(result.uri);
						Log.d("updateContactDB", "Apply batch rawID" + rawID);
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
		}
	}

}
