package com.coinxlab.fx;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.coinxlab.payment.error.PaymentException;

@Service
public class FxRateManager {
	
	String fxUrl = "http://free.currencyconverterapi.com/api/v5/convert?q=USD_INR&compact=y";
	private static float DEFAULT_RATE = 68.0f;

	public float getINRRate() throws PaymentException {
		RestTemplate restTemplate = new RestTemplate(); 
		FxResponse response = restTemplate.getForObject(fxUrl, FxResponse.class);
		String value = response.getFxResult().getVal();
		if(StringUtils.isEmpty(value)) throw new PaymentException("Error processing Fx request from " + fxUrl);
		return Float.parseFloat(value);
	}
	
	public float getDefaultINRRate() {
		return DEFAULT_RATE;
	}
}
