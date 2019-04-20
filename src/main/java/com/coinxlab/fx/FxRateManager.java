package com.coinxlab.fx;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.coinxlab.payment.error.PaymentException;

@Service
public class FxRateManager {
	
	String fxUrl = "https://free.currencyconverterapi.com/api/v6/convert?q=USD_INR&compact=ultra&apiKey=9f37a5fc87948bd398fa";//"http://free.currencyconverterapi.com/api/v5/convert?q=USD_INR&compact=y";
	private static float DEFAULT_RATE = 0.0f;

	public float getINRRate() throws PaymentException {
		RestTemplate restTemplate = new RestTemplate(); 
		String response = restTemplate.getForObject(fxUrl, String.class);
		JSONParser parser = new JSONParser();
		JSONObject jsonObject;
		try {
			jsonObject = (JSONObject)parser.parse(response);
			Double value = (Double)jsonObject.get("USD_INR");
			//String value = response.getFxResult().getVal();
			if(StringUtils.isEmpty(value)) throw new PaymentException("Error processing Fx request from " + fxUrl);
			return value.floatValue();
		} catch (ParseException e) {
			throw new PaymentException("Error processing Fx request from " + fxUrl);
		}
		
	}
	
	public float getDefaultINRRate() {
		return DEFAULT_RATE;
	}
}
