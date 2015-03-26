package com.graduation.phone.contacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.graduation.phone.contacts.ContactUtils.ContactInfoAndID;
import com.graduation.phone.contacts.ContactUtils.ContactStrInfoAndID;

public class AddOrUpdateNewContactOperate {

	public AddOrUpdateNewContactOperate() {
		// TODO Auto-generated constructor stub
	}
	public boolean execute(ContactInfo contactInfo)
	{
		boolean flag=false;
		boolean isExist=false;
		List<ContactStrInfoAndID> toUpdateContactsList=new ArrayList<ContactUtils.ContactStrInfoAndID>();
		List<ContactStrInfoAndID> toInsertContactsList=new ArrayList<ContactUtils.ContactStrInfoAndID>();
		ContactStrInfoAndID contactStrInfoAndID=new ContactStrInfoAndID();
		LinkedHashMap<Integer, ContactInfoAndID> localContacts = ContactUtils
				.getPhoneContacts();
		Iterator<ContactInfoAndID> iterator=localContacts.values().iterator();
		while(iterator.hasNext())
		{
			ContactInfoAndID info=(ContactInfoAndID)iterator.next();
			if(info.contactInfo.getName().equals(contactInfo.getName()))
			{
				isExist=true;
				contactStrInfoAndID.cid=info.cid;
				contactStrInfoAndID.vcard=contactInfo.toString();
				toUpdateContactsList.add(contactStrInfoAndID);
				ContactUtils.updateContactDB(toUpdateContactsList);
				flag=true;
				break;
			}
		}
		if(!isExist)
		{
			contactStrInfoAndID.vcard=contactInfo.toString();
			toUpdateContactsList.add(contactStrInfoAndID);
			ContactUtils.insertContactToDB(toInsertContactsList);
			flag=true;
		}
		return flag;
	}

}
