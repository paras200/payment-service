package com.coinxlab.payment.model;

import java.util.List;

public class RateCard {

	private List<FxRate> fxRates = FxRate.getRateList();
	private float payPalTxCharge = 2.5f ;
	private float paytmTxCharge = 1.0f;
	
	
	public List<FxRate> getFxRates() {
		fxRates = FxRate.getRateList();
		return fxRates;
	}
	
	public float getPayPalTxCharge() {
		return payPalTxCharge;
	}
	public void setPayPalTxCharge(float payPalTxCharge) {
		this.payPalTxCharge = payPalTxCharge;
	}
	public float getPaytmTxCharge() {
		return paytmTxCharge;
	}
	public void setPaytmTxCharge(float paytmTxCharge) {
		this.paytmTxCharge = paytmTxCharge;
	}
	
	
}
