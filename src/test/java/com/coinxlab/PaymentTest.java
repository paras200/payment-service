package com.coinxlab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coinxlab.payment.controller.PaymentController;
import com.coinxlab.payment.error.PaymentException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class PaymentTest {

	@Autowired
	private PaymentController controller;
	
	//@Test
	public void testCreditTranferSum() throws PaymentException {
	 String credit =	controller.getTotalCreditExchanged();
	 System.out.println("credit amt : " + credit);
	}
}
