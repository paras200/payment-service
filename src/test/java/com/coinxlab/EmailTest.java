package com.coinxlab;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coinxlab.email.EmailClient;
import com.coinxlab.payment.error.PaymentException;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class EmailTest {

	//@Autowired
	private EmailClient emailClient;
	
	//@Test
	public void testEmail() throws PaymentException {
		emailClient.sendWithdrawalConfirmation("coinxlab@gmail.com", 10d);
	}
}
