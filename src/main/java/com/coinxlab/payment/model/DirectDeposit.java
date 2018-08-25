package com.coinxlab.payment.model;

public class DirectDeposit {
	
	public static String STATUS_INPROGRESS = "INPROGRESS";
	public static String STATUS_COMPLETED = "COMPLETED";
	
	private Integer id;
	
	private String userId;
	private String userEmail;
	private double amount;
	private double credit;
	private double txFee;
	private String txReference;
	private String comments;
	private String status;
	private String ccy;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}
	public double getTxFee() {
		return txFee;
	}
	public void setTxFee(double txFee) {
		this.txFee = txFee;
	}
	public String getTxReference() {
		return txReference;
	}
	public void setTxReference(String txReference) {
		this.txReference = txReference;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	
}
