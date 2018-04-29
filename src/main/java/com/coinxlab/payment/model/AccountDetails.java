package com.coinxlab.payment.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.coinxlab.payment.error.PaymentException;

@Entity
public class AccountDetails {

	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	@Column(unique=true)
	private String userId;
	
	private String email;
	private Double amount;
	private Integer lastTxId;  //Deposit or Withdrawal
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Integer getLastTxId() {
		return lastTxId;
	}
	public void setLastTxId(Integer lastTxId) {
		this.lastTxId = lastTxId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Double add(Double amoutToAdd){
		this.amount = this.getAmount() + amoutToAdd;
		return this.amount;
	}
	
	public Double reduce(Double amoutToSubtract) throws PaymentException{
		if(amoutToSubtract > this.amount){
			throw new PaymentException("Insufficient balance.....");
		}
		this.amount = this.getAmount() - amoutToSubtract;
		return this.amount;
	}
	@Override
	public String toString() {
		return "AccountDetails [id=" + id + ", userId=" + userId + ", email=" + email + ", amount=" + amount
				+ ", lastTxId=" + lastTxId + "]";
	}
	
}
