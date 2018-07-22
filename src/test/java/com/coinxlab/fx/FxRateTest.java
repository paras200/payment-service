package com.coinxlab.fx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.coinxlab.payment.error.PaymentException;

import jdk.nashorn.internal.ir.annotations.Ignore;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class FxRateTest {
	
	//@Autowired
	private FxRateManager fxRateManager;
	
	@Ignore
	@Test
	public void fxRateTest() throws PaymentException {
		//float inrRate = fxRateManager.getINRRate();
		System.out.println(10);
	}
	
	

}
