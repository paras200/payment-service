package com.coinxlab.payment.error;

public class PaymentException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PaymentException(String errMessage) {
		super(errMessage);
	}
	
	public PaymentException(String errMessage, Exception ex) {
		super(errMessage,ex);
	}
}
