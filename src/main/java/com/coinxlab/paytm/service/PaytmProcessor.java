package com.coinxlab.paytm.service;

import java.util.TreeMap;

import org.springframework.stereotype.Service;

import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.paytm.PaytmConfig;
import com.paytm.pg.merchant.CheckSumServiceHelper;

@Service
public class PaytmProcessor {

	
	public String generateChecksum(TreeMap<String, String> parameters) throws Exception{
		CheckSumServiceHelper checksumHelper = CheckSumServiceHelper.getCheckSumServiceHelper();
		String checkSum =  checksumHelper.genrateCheckSum(PaytmConfig.MERCHANT_KEY_V, parameters);
		return checkSum;
	}
	
	public boolean verifyChecksum(TreeMap<String, String> paramap, String responseChceksum) throws PaymentException {
		CheckSumServiceHelper checksumHelper = CheckSumServiceHelper.getCheckSumServiceHelper();
		try {
			return checksumHelper.verifycheckSum(PaytmConfig.MERCHANT_KEY_V, paramap, responseChceksum);
		} catch (Exception e) {			
			e.printStackTrace();
			throw new PaymentException("Error validating checksum", e);
		}
	}
}
