package com.graduation.phone;


public class ContactBody {
	private int cid;
	private int rid;
	private String vcard;
	private int action;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public String getVcard() {
		return vcard;
	}

	public void setVcard(String vcard) {
		this.vcard = vcard;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public ContactBody() {

	}

}
