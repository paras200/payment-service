package com.coinxlab.payment.model;

import java.util.ArrayList;
import java.util.List;

public class FxRate {

	private String ccy;
	private float depositRate;
	private float withdrawalRate;
	
	private float fxConversionRate = 5 ; // in percentage
	
	enum CURRENCY{
		   INR, USD;
	}
	
	public FxRate() {
		
	}
	public FxRate(String ccy, float value) {
		this.ccy = ccy;
		if(CURRENCY.USD.toString().equalsIgnoreCase(ccy)) {
			this.depositRate = value;
			this.withdrawalRate = value;
		}else {
			this.depositRate = value * (1 + (fxConversionRate/100));
			this.withdrawalRate = value * (1 - (fxConversionRate/100));
		}
	}
	
	public String getCcy() {
		return ccy;
	}
	
	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
	
	public float getDepositRate() {
		return depositRate;
	}
	public void setDepositRate(float value) {
		this.depositRate = value;
	}

	public float getWithdrawalRate() {
		return withdrawalRate;
	}

	public void setWithdrawalRate(float withdrawalRate) {
		this.withdrawalRate = withdrawalRate;
	}

	public float getFxConversionRate() {
		return fxConversionRate;
	}

	public void setFxConversionRate(float fxConversionRate) {
		this.fxConversionRate = fxConversionRate;
	}

	public  List<FxRate> getRateList(float inrRate){
		FxRate rc1 = new FxRate(CURRENCY.INR.toString(), inrRate);
		FxRate rc2 = new FxRate(CURRENCY.USD.toString(), 1);
		List<FxRate> rList = new ArrayList<>();
		rList.add(rc1);
		rList.add(rc2);
		return rList;
	}
}
