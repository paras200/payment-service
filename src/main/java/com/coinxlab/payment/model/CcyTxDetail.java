package com.coinxlab.payment.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CcyTxDetail {
	
	public static String SYSTEM_PAYPAL ="paypal";  
	public static String SYSTEM_PAYTM ="paytm";
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String userId;
	private String userEmail;
	private String paypalUserEmail;
	private Double creditAmount; 
	
	private double totalAmount;
	private double txCharge;
	private double txAmount;
	private String txCCY;
	
	private Date createdAt  = new Date();
	private String txId;
	private String status ;
	
	
	private String paymentSystem;
	
	private String fileRefernece;


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

	public String getPaypalUserEmail() {
		return paypalUserEmail;
	}

	public void setPaypalUserEmail(String paypalUserEmail) {
		this.paypalUserEmail = paypalUserEmail;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTxCharge() {
		return txCharge;
	}

	public void setTxCharge(double txCharge) {
		this.txCharge = txCharge;
	}

	public double getTxAmount() {
		return txAmount;
	}

	public void setTxAmount(double txAmount) {
		this.txAmount = txAmount;
	}

	public String getTxCCY() {
		return txCCY;
	}

	public void setTxCCY(String txCCY) {
		this.txCCY = txCCY;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPaymentSystem() {
		return paymentSystem;
	}

	public void setPaymentSystem(String paymentSystem) {
		this.paymentSystem = paymentSystem;
	}

	public String getFileRefernece() {
		return fileRefernece;
	}

	public void setFileRefernece(String fileRefernece) {
		this.fileRefernece = fileRefernece;
	}
	
}
