package com.coinxlab.payment.model;

import java.util.ArrayList;
import java.util.List;

public class FxRate {

	private String ccy;
	private float value;
	private float txCharge = 2.5f;
	
	enum CURRENCY{
		   INR, USD;
	}
	
	public FxRate(String ccy, float value) {
		this.ccy = ccy;
		this.value = value;
	}
	
	public String getCcy() {
		return ccy;
	}
	
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	
	public float getTxCharge() {
		return txCharge;
	}

	public void setTxCharge(float txCharge) {
		this.txCharge = txCharge;
	}

	public static  List<FxRate> getRateList(){
		FxRate rc1 = new FxRate(CURRENCY.INR.toString(), 60);
		FxRate rc2 = new FxRate(CURRENCY.USD.toString(), 1);
		List<FxRate> rList = new ArrayList<>();
		rList.add(rc1);
		rList.add(rc2);
		return rList;
	}
}
