package com.coinxlab.payment.model;

import java.util.ArrayList;
import java.util.List;

public class RateCard {

	private String ccy;
	private float value;
	private float txCharge = 2.5f;
	
	enum CURRENCY{
		   INR, USD, EUR, GBP;
	}
	
	public RateCard(String ccy, float value) {
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

	public static  List<RateCard> getRateList(){
		RateCard rc1 = new RateCard(CURRENCY.INR.toString(), 60);
		RateCard rc2 = new RateCard(CURRENCY.USD.toString(), 1);
		RateCard rc3 = new RateCard(CURRENCY.GBP.toString(), 0.90f);
		RateCard rc4 = new RateCard(CURRENCY.EUR.toString(), 1.2f);
		List<RateCard> rList = new ArrayList<>();
		rList.add(rc1);
		rList.add(rc2);
		rList.add(rc3);
		rList.add(rc4);
		return rList;
	}
}
