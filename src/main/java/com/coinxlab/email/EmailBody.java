package com.coinxlab.email;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmailBody {

	private String template;
	private String subject;
	private String body;
	private Map<String, String> paramMap;
	List<String> toList;
	
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	
	public List<String> getToList() {
		if(toList == null) 
			toList = new ArrayList<>();
		return toList;
	}
	public void setToList(List<String> toList) {
		this.toList = toList;
	}	
}
