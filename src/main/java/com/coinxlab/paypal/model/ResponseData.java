package com.coinxlab.paypal.model;

import java.util.Date;

public class ResponseData {
	private String redirect;
	private String appToken = new Date().toString();


	public ResponseData(String redirect) {
		super();
		this.redirect = redirect;
		this.appToken = new Date().toString();
	}
	
	public ResponseData(String redirect, String appToken) {
		super();
		this.redirect = redirect;
		this.appToken = appToken;
	}

	public String getAppToken() {
		return appToken;
	}

	public void setAppToken(String appToken) {
		this.appToken = appToken;
	}

	public String getRedirect() {
		return redirect;
	}

	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	

}
