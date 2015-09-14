package com.bear.demo.LBS;

public class LBS_Local_Content {
	private String address;
	private LBS_Local_detail address_detail;
	private LBS_Local_Point point;
	
	public String getAddress() {
		return address;
	}
	public LBS_Local_detail getAddress_detail() {
		return address_detail;
	}
	public LBS_Local_Point getPoint() {
		return point;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setAddress_detail(LBS_Local_detail address_detail) {
		this.address_detail = address_detail;
	}
	public void setPoint(LBS_Local_Point point) {
		this.point = point;
	}
}