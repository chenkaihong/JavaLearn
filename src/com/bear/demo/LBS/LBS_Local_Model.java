package com.bear.demo.LBS;

public class LBS_Local_Model {
	private String address;
	private LBS_Local_Content content;
	private int status;
	
	public String getAddress() {
		return address;
	}
	public LBS_Local_Content getContent() {
		return content;
	}
	public int getStatus() {
		return status;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setContent(LBS_Local_Content content) {
		this.content = content;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
