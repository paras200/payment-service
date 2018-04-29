package com.coinxlab.payment.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TxDetails {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private String custId;
	
	@Column(unique=true)
	private String orderId;
	
	private String mobile;
	private String email;
	private Double txAmount;
	private String cksumRequest;
	private String ccy;
	private String status;
	private String respcode;
	private String respmsg;
	private String banktxId;
	private String cksumResponse;
	private Date createdAt = Calendar.getInstance().getTime(); ;
	private Date lastUpdated ;
	private long lastUpdatedTimeinMilli = Calendar.getInstance().getTimeInMillis();
	private String result;	
	private String txnId;
	private String txnType;
	private String gatewayName;
	private String bankName;
	private String paymentMode;
	private String txnDate;
	private Double refundAmt;
	private Double respTxAmount;
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Double getTxAmount() {
		return txAmount;
	}
	public void setTxAmount(Double txAmount) {
		this.txAmount = txAmount;
	}
	public String getCksumRequest() {
		return cksumRequest;
	}
	public void setCksumRequest(String cksumRequest) {
		this.cksumRequest = cksumRequest;
	}
	public String getCcy() {
		return ccy;
	}
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRespcode() {
		return respcode;
	}
	public void setRespcode(String respcode) {
		this.respcode = respcode;
	}
	public String getRespmsg() {
		return respmsg;
	}
	public void setRespmsg(String respmsg) {
		this.respmsg = respmsg;
	}
	public String getBanktxId() {
		return banktxId;
	}
	public void setBanktxId(String banktxId) {
		this.banktxId = banktxId;
	}
	public String getCksumResponse() {
		return cksumResponse;
	}
	public void setCksumResponse(String cksumResponse) {
		this.cksumResponse = cksumResponse;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public Integer getId() {
		return id;
	}
	
	public long getLastUpdatedTimeinMilli() {
		return lastUpdatedTimeinMilli;
	}
	public void setLastUpdatedTimeinMilli(long lastUpdatedTimeinMilli) {
		this.lastUpdatedTimeinMilli = lastUpdatedTimeinMilli;
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
		
	public String getTxnId() {
		return txnId;
	}
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getTxnDate() {
		return txnDate;
	}
	public void setTxnDate(String txnDate) {
		this.txnDate = txnDate;
	}
	public Double getRefundAmt() {
		return refundAmt;
	}
	public void setRefundAmt(Double refundAmt) {
		this.refundAmt = refundAmt;
	}
	
	public Double getRespTxAmount() {
		return respTxAmount;
	}
	public void setRespTxAmount(Double respTxAmount) {
		this.respTxAmount = respTxAmount;
	}
	@Override
	public String toString() {
		return "TxDetails [id=" + id + ", custId=" + custId + ", orderId=" + orderId + ", mobile=" + mobile + ", email="
				+ email + ", txAmount=" + txAmount + ", cksumRequest=" + cksumRequest + ", ccy=" + ccy + ", status="
				+ status + ", respcode=" + respcode + ", respmsg=" + respmsg + ", banktxId=" + banktxId
				+ ", cksumResponse=" + cksumResponse + ", createdAt=" + createdAt + ", lastUpdated=" + lastUpdated
				+ ", lastUpdatedTimeinMilli=" + lastUpdatedTimeinMilli + ", result=" + result + ", txnId=" + txnId + ", txnType=" + txnType + ", gatewayName=" + gatewayName
				+ ", bankName=" + bankName + ", paymentMode=" + paymentMode + ", txnDate=" + txnDate + ", refundAmt="
				+ refundAmt + "]";
	}
	
	
}
