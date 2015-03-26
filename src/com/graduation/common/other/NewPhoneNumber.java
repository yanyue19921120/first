package com.graduation.common.other;

import com.graduation.phone.contacts.ContactInfo;

public class NewPhoneNumber {

	private boolean isNewNumber=false;
	private ContactInfo contactInfo;
	public NewPhoneNumber() {
		// TODO Auto-generated constructor stub
		contactInfo=new ContactInfo();
	}

	public boolean isNewNumber() {
		return isNewNumber;
	}

	public void setNewNumber(boolean isNewNumber) {
		this.isNewNumber = isNewNumber;
	}

	public ContactInfo getContactInfo() {
		return contactInfo;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		// TODO Auto-generated method stub
		this.contactInfo = contactInfo;
	}

}
