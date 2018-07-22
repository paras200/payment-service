package com.coinxlab.common;

import java.util.Date;

public class Result {
	
	public static String STATUS_SUCCESS = "SUCCESS";
	public static String STATUS_FAIL = "FAIL";
	
	private String token = new Date().toString();
	private String status;
	
	public Result(String data) {
		this.status = data;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String data) {
		this.status = data;
	}
	
	

}
