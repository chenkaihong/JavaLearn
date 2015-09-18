package com.bear.demo.LBS;

public class Teacher {
	private long id;
	private String name;
	private double lat;
	private double lon;
	private String geoHash;
	
	public Teacher(long id, String name, double lat, double lon) {
		super();
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
	
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public String getGeoHash() {
		return geoHash;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public void setGeoHash(String geoHash) {
		this.geoHash = geoHash;
	}
}
