package com.coinxlab.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtil {

	public static String roundDouble(Double num) {
		if(num == null) return "0";
		BigDecimal bigDecimal = new BigDecimal(num);
		BigDecimal bd = bigDecimal.setScale(2,RoundingMode.CEILING);
		return bd.toPlainString();
	}
	
	public static String floatToString(float value) {
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		String output = myFormatter.format(value);
		//System.out.println(value + "   -  " + output);
		return output;
	}
	
	public static void main(String[] args) {
		Double amount = 10d;
		
		System.out.println(roundDouble(amount));
		
		System.out.println(floatToString((float)10/50));
				
	}
}
