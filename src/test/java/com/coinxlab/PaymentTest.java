package com.coinxlab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coinxlab.common.Result;
import com.coinxlab.payment.controller.PaymentController;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.DirectDeposit;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class PaymentTest {

	@Autowired
	private PaymentController controller;
	
	//@Test
	public void testCreditTranferSum() throws PaymentException {
	// Result credit =	controller.getTotalCreditExchanged() ;
	 //System.out.println("credit amt : " + credit);
	}
	
	//@Test
	/*public void testConfirmDeposit() throws PaymentException {
		DirectDeposit directDeposit = new  DirectDeposit();
		directDeposit.setCcy("USD");
		directDeposit.setCredit(100);
		directDeposit.setUserId("sinhanil19@gamil.com");
		controller.deposit(directDeposit);
		controller.validatedDirectDeposit(1, "sinhanil19@gamil.com", "saketkhairnar@gmail.com");
	}*/
}
