package com.graduation.common.maintask;

import static com.graduation.common.maintask.BackupContactsTask.COMPARE_ADD_END;
import static com.graduation.common.maintask.BackupContactsTask.INSERT_DB_END;
import static com.graduation.common.maintask.BackupContactsTask.LOCAL_CONTACT_ADD_END;
import static com.graduation.common.maintask.BackupContactsTask.LOCAL_READ_PERCENT;
import static com.graduation.common.maintask.BackupContactsTask.READ_VERSION_END;
import static com.graduation.common.maintask.BackupContactsTask.REMOTE_COMPARE_PERCENT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

import com.graduation.common.GraduationConst;
import com.graduation.phone.ContactBody;
import com.graduation.phone.LContact;
import com.graduation.phone.contacts.ContactListen;
import com.graduation.phone.contacts.ContactUtils;
import com.graduation.phone.contacts.ContactUtils.ContactStrInfoAndID;
import com.graduation.phone.operate.NetOperate;

public class ContactsOperation {

	private Handler mHandler;
	private int timer_count = 0;

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public void sendSynProgress(float pro_val) {
		Message msg = mHandler.obtainMessage(GraduationConst.SYNC_CONTACTS,
				pro_val);
		mHandler.sendMessage(msg);
	}
	public void sendSynErrorMessage()
	{
		Message msg=mHandler.obtainMessage(GraduationConst.SYNC_CONTACTS_ERROR);
		mHandler.sendMessage(msg);
	}

	public void sendRestoreProgress(float pro_val) {
		Message msg = mHandler.obtainMessage(GraduationConst.RESTORE_CONTACTS,
				pro_val);
		mHandler.sendMessage(msg);
	}

	public ContactsOperation() {

	}

	// backup OR sync
	public void backupContacts() {
		// LContact lContact = download();
		//sendSynProgress(0);
		LContact lContact = new LContact();
		lContact = NetOperate.download();
		List<ContactBody> netContactsList = lContact.contactList;
		// Log.v("backup", lContact.msg);
		LinkedHashMap<Integer, ContactStrInfoAndID> localContacts = ContactUtils
				.getPhoneContactsAndID(this, true);
		ContactListen contactListen = new ContactListen();
		List<String> changedContacts = contactListen.changedContacts;
		List<String> deletedContacts = contactListen.deletedContacts;
		// 待上传的新通讯录
		List<ContactStrInfoAndID> newContactsList = new ArrayList<ContactStrInfoAndID>();
		List<Integer> toDeleteContactsList = new ArrayList<Integer>();
		List<ContactStrInfoAndID> toInsertContactsList = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		List<ContactStrInfoAndID> toUpdateContactsList = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		List<ContactStrInfoAndID> deletedContactsList = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		// List<ContactBody> temps = netContactsList;
		List<ContactBody> temps = new ArrayList<ContactBody>();
		// 循环有相同cid情况

		int contentSize = netContactsList.size();
		float provalue = 0;
		int compareSize = 0;
		int ruleSize = contentSize > 1000 ? 20 : contentSize > 500 ? 10
				: contentSize > 100 ? 5 : 1;

		for (ContactBody contactBody : netContactsList) {
			int action = contactBody.getAction();
			int cid = contactBody.getCid();
			int rid = contactBody.getRid();
			String vcard = contactBody.getVcard();

			if (compareSize % ruleSize == 0) {
				provalue = Float.valueOf(compareSize
						* (REMOTE_COMPARE_PERCENT - LOCAL_READ_PERCENT)
						/ contentSize);
				sendSynProgress(provalue + LOCAL_READ_PERCENT);

			}

			if (localContacts.containsKey(cid)) {
				ContactStrInfoAndID infoAndID = localContacts.get(cid);
				infoAndID.rid = rid;
				boolean isLocalChanged = changedContacts.contains(String.valueOf(cid));
				boolean isRemoteChanged = !vcard.equals(infoAndID.vcard)
						&& !isLocalChanged;

				if (action == 1 && !isLocalChanged) {
					infoAndID.action = 1;
					toDeleteContactsList.add(cid);
					deletedContactsList.add(infoAndID);
				} else if (action == 1 && isLocalChanged) {
					infoAndID.action = 0;
					newContactsList.add(infoAndID);
				} else if (isRemoteChanged && !isLocalChanged) {
					infoAndID.action = 0;
					infoAndID.vcard = vcard;
					toUpdateContactsList.add(infoAndID);
				}else if(isLocalChanged&&!isRemoteChanged) {
					infoAndID.action = 2;
					newContactsList.add(infoAndID);
				}
				else {
					newContactsList.add(infoAndID);
				}
				localContacts.remove(cid);
			} else {
				temps.add(contactBody);
			}
			compareSize++;
		}
		sendSynProgress(REMOTE_COMPARE_PERCENT);
		// 处理cid不相同，但是vcard相同的情况
		List<ContactBody> newcontents = new ArrayList<ContactBody>(temps);
		
		int insert_contentSize = netContactsList.size();
		float insert_provalue = 0;
		int insertSize = 0;
		int insert_ruleSize = insert_contentSize > 1000 ? 20
				: insert_contentSize > 500 ? 10 : insert_contentSize > 100 ? 5
						: 1;
				
		for (ContactBody contact : temps) {
			
			if (insertSize % insert_ruleSize == 0) {
				insert_provalue = Float.valueOf(insertSize
						* (COMPARE_ADD_END - REMOTE_COMPARE_PERCENT)
						/ insert_contentSize);
				sendSynProgress(insert_provalue + REMOTE_COMPARE_PERCENT);
			}
			
			Iterator<ContactStrInfoAndID> iterator = localContacts.values()
					.iterator();
			while (iterator.hasNext()) {
				ContactStrInfoAndID contactStrInfoAndID = (ContactStrInfoAndID) iterator
						.next();
				if (contact.getVcard().equals(contactStrInfoAndID.vcard)) {
					if (contact.getAction() == 1) {
						toDeleteContactsList.add(contactStrInfoAndID.cid);
						contactStrInfoAndID.action = 1;
						contactStrInfoAndID.rid = contact.getRid();
						deletedContactsList.add(contactStrInfoAndID);
					} else {
						contactStrInfoAndID.rid = contact.getRid();
					}
					newcontents.remove(contact);
					localContacts.remove(contactStrInfoAndID.cid);
					break;
				}
			}
			insertSize++;

		}
		sendSynProgress(COMPARE_ADD_END);
		// 处理远程有数据，而本地没有数据到本地（其中可能包含本地已经删除数据）
		temps = new ArrayList<ContactBody>(newcontents);
		
		int local_add_contentSize = newcontents.size();
		float local_add_provalue = 0;
		int localInsertSize = 0;
		int local_add_ruleSize = local_add_contentSize > 1000 ? 20
				: local_add_contentSize > 500 ? 10
						: local_add_contentSize > 100 ? 5 : 1;
						
						
		for (ContactBody contact : temps) {
			

			if (localInsertSize % local_add_ruleSize == 0) {
				local_add_provalue = Float.valueOf(localInsertSize
						* (LOCAL_CONTACT_ADD_END - COMPARE_ADD_END)
						/ local_add_contentSize);
				sendSynProgress(local_add_provalue + COMPARE_ADD_END);
			}
			
			ContactStrInfoAndID info = new ContactStrInfoAndID();
			info.vcard = contact.getVcard();
			info.rid = contact.getRid();
			info.cid = contact.getCid();
			if (deletedContacts.contains(String.valueOf(contact.getCid()))) {

				info.action = 1;
				deletedContactsList.add(info);
				if (contact.getAction() == 0) {
					// do nothing
				}
			} else if (contact.getAction() == 1) {
				info.action = 1;
				newContactsList.add(info);
			} else {
				toInsertContactsList.add(info);
			}
			newcontents.remove(contact);
			
			localInsertSize++;
		}
		sendSynProgress(LOCAL_CONTACT_ADD_END);
		// 处理本地有数据远程没有数据到远程
		newContactsList.addAll(localContacts.values());
		

		final int operateCount = toInsertContactsList.size()
				+ toDeleteContactsList.size() + toUpdateContactsList.size();

		// task
		TimerTask insertDbTask = new TimerTask() {
			@Override
			public void run() {
				if (timer_count < operateCount) {
					timer_count++;
					float f = Float
							.valueOf((INSERT_DB_END - LOCAL_CONTACT_ADD_END)
									* timer_count / operateCount);
					sendSynProgress(f + LOCAL_CONTACT_ADD_END);
				}
			}
		};
		Timer mTimer = new Timer();
		timer_count = 0;
		mTimer.scheduleAtFixedRate(insertDbTask, 500, 500);

		ContactUtils.insertContactToDB(toInsertContactsList);
		ContactUtils.deleteContactsDB(toDeleteContactsList);
		ContactUtils.updateContactDB(toUpdateContactsList);
		//sendSynProgress(INSERT_DB_END);
		newContactsList.addAll(toInsertContactsList);
		newContactsList.addAll(toUpdateContactsList);
		newContactsList.addAll(deletedContactsList);
		
		
		// 上传数据
		
		boolean isUpdate=NetOperate.upload(newContactsList);
		mTimer.cancel();
		if(isUpdate) {
		contactListen.saveIDAndVersion();
		sendSynProgress(READ_VERSION_END);
		
		}
		else
		{
			sendSynErrorMessage();
		}
		
	}

	public void restore() {
		LContact lContact = new LContact();
		lContact = NetOperate.download();
		List<ContactBody> netContactsList = lContact.contactList;
		List<ContactBody> netContactsListTemp = new ArrayList<ContactBody>(
				netContactsList);
		LinkedHashMap<Integer, ContactStrInfoAndID> localContacts = ContactUtils
				.getPhoneContactsAndID(this, false);
		List<ContactStrInfoAndID> localContactsAdd = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		List<Integer> localContactsDel = new ArrayList<Integer>();
		List<ContactStrInfoAndID> localContactsUpdate = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		// 最终上传的通讯录
		List<ContactStrInfoAndID> newLocalContacts = new ArrayList<ContactUtils.ContactStrInfoAndID>();
		
		int contentSize = netContactsList.size();
		float provalue = 0;
		int compareSize = 0;
		int ruleSize = contentSize > 1000 ? 20 : contentSize > 500 ? 10
				: contentSize > 100 ? 5 : 1;

		for (int i = 0; i < netContactsList.size(); i++) {
			
			if (compareSize % ruleSize == 0) {
				provalue = Float.valueOf(compareSize
						* (REMOTE_COMPARE_PERCENT - LOCAL_READ_PERCENT)
						/ contentSize);
				sendRestoreProgress(provalue + LOCAL_READ_PERCENT);

			}
			
			ContactBody contact = netContactsList.get(i);
			int cid = contact.getCid();
			int action = contact.getAction();
			int rid = contact.getRid();
			String vcard = contact.getVcard();
			if (localContacts.containsKey(cid)) {
				ContactStrInfoAndID infoAndID = localContacts.get(cid);
				if (action == 1) {
					localContactsDel.add(cid);
					infoAndID.rid = rid;
					newLocalContacts.add(infoAndID);
				} else {
					if (infoAndID.vcard.equals(vcard)) {
						infoAndID.rid = rid;
						newLocalContacts.add(infoAndID);

					} else {
						infoAndID.vcard = vcard;
						infoAndID.rid = rid;
						localContactsUpdate.add(infoAndID);
						newLocalContacts.add(infoAndID);
					}
				}
				localContacts.remove(cid);
				netContactsListTemp.remove(contact);
			}
			compareSize++;

		}

		// 处理本地通讯录和远程的关联
		netContactsList = new ArrayList<ContactBody>(netContactsListTemp);
		
		int insert_contentSize = netContactsList.size();
		float insert_provalue = 0;
		int insertSize = 0;
		int insert_ruleSize = insert_contentSize > 1000 ? 20
				: insert_contentSize > 500 ? 10 : insert_contentSize > 100 ? 5
						: 1;


		for (ContactBody contact : netContactsList) {
			
			if (insertSize % insert_ruleSize == 0) {
				insert_provalue = Float.valueOf(insertSize
						* (COMPARE_ADD_END - REMOTE_COMPARE_PERCENT)
						/ insert_contentSize);
				sendRestoreProgress(insert_provalue + REMOTE_COMPARE_PERCENT);
			}
			
			Iterator<ContactStrInfoAndID> iterator = localContacts.values()
					.iterator();
			while (iterator.hasNext()) {
				ContactStrInfoAndID infoAndID = (ContactStrInfoAndID) iterator
						.next();
				if (contact.getVcard().equals(infoAndID.vcard)) {
					if (contact.getAction() == 1) {
						localContactsDel.add(infoAndID.cid);

					}
					infoAndID.rid = contact.getRid();
					newLocalContacts.add(infoAndID);
					netContactsListTemp.remove(contact);
					localContacts.remove(infoAndID.cid);
					break;
				}
			}

			insertSize++;
		}

		// 云端剩余部分为本地新增
		int local_add_contentSize = netContactsListTemp.size();
		float local_add_provalue = 0;
		int localInsertSize = 0;
		int local_add_ruleSize = local_add_contentSize > 1000 ? 20
				: local_add_contentSize > 500 ? 10
						: local_add_contentSize > 100 ? 5 : 1;

		for (ContactBody contact : netContactsListTemp) {
			
			if (localInsertSize % local_add_ruleSize == 0) {
				local_add_provalue = Float.valueOf(localInsertSize
						* (LOCAL_CONTACT_ADD_END - COMPARE_ADD_END)
						/ local_add_contentSize);
				sendRestoreProgress(local_add_provalue + COMPARE_ADD_END);
			}

			ContactStrInfoAndID infoAndID = new ContactStrInfoAndID(contact);
			infoAndID.rid = contact.getRid();
			if (contact.getAction() == 1) {

				newLocalContacts.add(infoAndID);
			} else {
				localContactsAdd.add(infoAndID);
			}
			localInsertSize++;
		}

		// 本地删除

		Iterator<ContactStrInfoAndID> iterator = localContacts.values()
				.iterator();
		while (iterator.hasNext()) {
			ContactStrInfoAndID infoAndID = (ContactStrInfoAndID) iterator
					.next();
			localContactsDel.add(infoAndID.cid);
		}
		

		final int operateCount = localContactsAdd.size()
				+ localContactsDel.size() + localContactsUpdate.size();
		TimerTask insertDbTask = new TimerTask() {
			@Override
			public void run() {
				if (timer_count < operateCount) {
					timer_count++;
					float f = Float
							.valueOf((INSERT_DB_END - LOCAL_CONTACT_ADD_END)
									* timer_count / operateCount);
					sendRestoreProgress(f + LOCAL_CONTACT_ADD_END);
				}
			}
		};
		Timer mTimer = new Timer();
		timer_count = 0;
		mTimer.scheduleAtFixedRate(insertDbTask, 500, 500);

		ContactUtils.updateContactDB(localContactsUpdate);
		ContactUtils.insertContactToDB(localContactsAdd);
		ContactUtils.deleteContactsDB(localContactsDel);
		sendRestoreProgress(INSERT_DB_END);

		newLocalContacts.addAll(localContactsAdd);
		ContactListen changeClass = new ContactListen();
		changeClass.addedContacts.clear();
		changeClass.changedContacts.clear();
		changeClass.deletedContacts.clear();
		changeClass.saveIDAndVersion();

		NetOperate.upload(newLocalContacts);
		sendRestoreProgress(READ_VERSION_END);

	}

}
