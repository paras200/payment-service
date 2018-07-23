package com.coinxlab.payment.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.coinxlab.common.NumberUtil;

@Entity
public class PaymentDetails {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(name="sourceUserId") 
	private String sourceUserId;
	
	private String sourceUserEmail;
	private String destUserId;
	private String destUserEmail;
	private Double amount;
	private Double txCharge;
	private String paymentSystem;
	private String payReference;
	private String txType;
	private String enrolId;
	private Date date = Calendar.getInstance().getTime();
	private String description;
	private String roundedAmount;
	
	public Integer getId() {
		return id;
	}	
	public PaymentDetails copy() {
		PaymentDetails pd = new PaymentDetails();
		pd.setSourceUserEmail(sourceUserEmail);
		pd.setSourceUserId(sourceUserId);
		pd.setDestUserId(destUserId);
		pd.setDestUserEmail(destUserEmail);
		pd.setAmount(amount);
		pd.setTxCharge(txCharge);
		pd.setPaymentSystem(paymentSystem);
		pd.setPayReference(payReference);
		return pd;
	}
	public String getSourceUserId() {
		return sourceUserId;
	}
	public void setSourceUserId(String sourceUserId) {
		this.sourceUserId = sourceUserId;
	}
	public String getSourceUserEmail() {
		return sourceUserEmail;
	}
	public void setSourceUserEmail(String sourceUserEmail) {
		this.sourceUserEmail = sourceUserEmail;
	}
	public String getDestUserId() {
		return destUserId;
	}
	public void setDestUserId(String destUserId) {
		this.destUserId = destUserId;
	}
	public String getDestUserEmail() {
		return destUserEmail;
	}
	public void setDestUserEmail(String destUserEmail) {
		this.destUserEmail = destUserEmail;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getPaymentSystem() {
		return paymentSystem;
	}
	public void setPaymentSystem(String paymentSystem) {
		this.paymentSystem = paymentSystem;
	}
	public String getPayReference() {
		return payReference;
	}
	public void setPayReference(String payReference) {
		this.payReference = payReference;
	}	
	
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getEnrolId() {
		return enrolId;
	}
	public void setEnrolId(String enrolId) {
		this.enrolId = enrolId;
	}
	
	
	public Double getTxCharge() {
		return txCharge;
	}
	public void setTxCharge(Double txCharge) {
		this.txCharge = txCharge;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getRoundedAmount() {
		return NumberUtil.roundDouble(amount);
	}
	
	
	@Override
	public String toString() {
		return "PaymentDetails [id=" + id + ", sourceUserId=" + sourceUserId + ", sourceUserEmail=" + sourceUserEmail
				+ ", destUserId=" + destUserId + ", destUserEmail=" + destUserEmail + ", amount=" + amount
				+ ", paymentSystem=" + paymentSystem + ", payReference=" + payReference + ", txType=" + txType
				+ ", date=" + date + "]";
	}	
}
