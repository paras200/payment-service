package com.coinxlab.payment.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.redis.core.index.Indexed;
import org.springframework.lang.NonNull;

public class CashTx {

	public static String IN_PROGRESS ="IN_PROGRESS";
	public static String COMPLETED ="COMPLETED";
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@NonNull
	@Indexed
	private String userId;
	
	private Double creditAmt;
	private Double cashAmt;
	
	private String ccy;
	
	@NonNull
	private String txType;
	
	@Indexed
	private Date createdAt = Calendar.getInstance().getTime();
	
	private long lastUpdatedTimeinMilli = Calendar.getInstance().getTimeInMillis();
	
	private String adminId;
	
	private String comment;
	@Indexed
	private String status = IN_PROGRESS;

	public Integer getId() {
		return id;
	}

	public Double getCreditAmt() {
		return creditAmt;
	}

	public void setCreditAmt(Double creditAmt) {
		this.creditAmt = creditAmt;
	}

	public Double getCashAmt() {
		return cashAmt;
	}

	public void setCashAmt(Double cashAmt) {
		this.cashAmt = cashAmt;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public String getTxType() {
		return txType;
	}

	public void setTxType(String txType) {
		this.txType = txType;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getLastUpdatedTimeinMilli() {
		return lastUpdatedTimeinMilli;
	}

	public void setLastUpdatedTimeinMilli(long lastUpdatedTimeinMilli) {
		this.lastUpdatedTimeinMilli = lastUpdatedTimeinMilli;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CashTx [id=" + id + ", userId=" + userId + ", creditAmt=" + creditAmt + ", cashAmt=" + cashAmt + "]";
	} ;
	
	

}
