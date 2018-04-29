package com.coinxlab.payment.utils;

import org.springframework.util.StringUtils;

public class AppUtils {

	public static Double convertToDouble(String data){
		if(StringUtils.isEmpty(data))
			return 0.0;
		else 
			return Double.valueOf(data);
	}
}
