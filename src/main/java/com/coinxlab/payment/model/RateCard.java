
package com.coinxlab.payment.model;

import java.util.List;

public class RateCard {

	private List<FxRate> fxRates ;
	private float payPalTxCharge = 0.0f ;
	private float paytmTxCharge = 0.0f;
	private float directDepositTxCharge = 1.0f;
	private int txCharge = 5;
	private int minimumCreditBalance = 3;
	
	public RateCard(float inrRate) {
		initFxRate(inrRate);/// set fxrate 
	}
	
	public void initFxRate(float inrRate) {
		FxRate fr = new FxRate();
		fxRates = fr.getRateList(inrRate);
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

	public float getDirectDepositTxCharge() {
		return directDepositTxCharge;
	}

	public void setDirectDepositTxCharge(float directDepositTxCharge) {
		this.directDepositTxCharge = directDepositTxCharge;
	}

	public List<FxRate> getFxRates() {
		return fxRates;
	}

	public void setFxRates(List<FxRate> fxRates) {
		this.fxRates = fxRates;
	}

	public int getTxCharge() {
		return txCharge;
	}

	public void setTxCharge(int txCharge) {
		this.txCharge = txCharge;
	}

	public int getMinimumCreditBalance() {
		return minimumCreditBalance;
	}

	public void setMinimumCreditBalance(int minimumCreditBalance) {
		this.minimumCreditBalance = minimumCreditBalance;
	}
	
	
}
