package com.coinxlab.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtil {

	public static String roundDouble(Double num) {
		BigDecimal bigDecimal = new BigDecimal(num);
		BigDecimal bd = bigDecimal.setScale(2,RoundingMode.CEILING);
		return bd.toPlainString();
	}
}
