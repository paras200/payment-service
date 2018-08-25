package com.coinxlab.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

	public static String roundDouble(Double num) {
		if(num == null) return "0";
		BigDecimal bigDecimal = new BigDecimal(num);
		BigDecimal bd = bigDecimal.setScale(2,RoundingMode.CEILING);
		return bd.toPlainString();
	}
	
	public static void main(String[] args) {
		Double amount = 10d;
		
		System.out.println(roundDouble(amount));
				
	}
}
