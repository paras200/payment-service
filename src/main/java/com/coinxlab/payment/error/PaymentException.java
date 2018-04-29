package com.coinxlab.payment.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="Error processing the request")
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
