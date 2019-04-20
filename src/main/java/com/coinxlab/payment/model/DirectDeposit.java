package com.coinxlab.payment.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.redis.core.index.Indexed;

@Entity
public class DirectDeposit {
	
	public static String STATUS_INPROGRESS = "INPROGRESS";
	public static String STATUS_COMPLETED = "COMPLETED";
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer ccyTxId;
	
	private String userId;
	private String userEmail;
	private double amount;
	private double credit;
	private double txFee;
	
	@Column(unique=true)
	private String txReference;
	
	private String comments;
	
	@Indexed
	private String status = STATUS_INPROGRESS;
	private String ccy;
	private String modeoftransfer;
	private Double fxRate;
	private Date createdDate = Calendar.getInstance().getTime();
	private Date lastUpdatedAt = new Date();
	private Date transactionDate;
	private double bankAcBalance;;
	
	private String updatedBy;
	
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
	public String getModeoftransfer() {
		return modeoftransfer;
	}
	public void setModeoftransfer(String modeoftransfer) {
		this.modeoftransfer = modeoftransfer;
	}
	public Double getFxRate() {
		return fxRate;
	}
	public void setFxRate(Double fxRate) {
		this.fxRate = fxRate;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}
	public Integer getCcyTxId() {
		return ccyTxId;
	}
	public void setCcyTxId(Integer ccyTxId) {
		this.ccyTxId = ccyTxId;
	}
	public double getBankAcBalance() {
		return bankAcBalance;
	}
	public void setBankAcBalance(double bankAcBalance) {
		this.bankAcBalance = bankAcBalance;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}
	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}
	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}
	
}
