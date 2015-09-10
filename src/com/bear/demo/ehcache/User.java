package com.bear.demo.ehcache;

import java.io.Serializable;

public class User implements Serializable {

	private static final long serialVersionUID = -4402392412217726278L;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}