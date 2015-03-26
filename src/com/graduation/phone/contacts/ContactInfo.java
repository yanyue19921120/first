package com.graduation.phone.contacts;
import java.util.ArrayList;
import java.util.List;
/**
 *This class contain contacts information
 *1.contacts's name
 *2.contacts's phone list 
 * @author shenxy
 */
public class ContactInfo {
	
	private String name ;
	public static class PhoneInfo
	{
		public int type;
		public String number;
	}
	/**
	 * contacts phone list
	 * note£» Cause some contacts has more than one phone number
	 **/
	private List<PhoneInfo> phoneList=new ArrayList<PhoneInfo>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<PhoneInfo> getPhoneList() {
		return phoneList;
	}
	public void setPhoneList(List<PhoneInfo> phoneList) {
		this.phoneList = phoneList;
	}
	@Override
	public String toString() {
		return ContactUtils.Contact2String(this);
	}

}
