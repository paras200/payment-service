package com.coinxlab.paytm;

import java.util.Map;
import java.util.TreeMap;

import com.paytm.pg.merchant.CheckSumServiceHelper;

public class PaytmConfig {

	public final static String MID_V="Naigle88461693677550";
	public final static String MERCHANT_KEY_V="Fz5Gz_&ouJcFIL1g";
	public final static String INDUSTRY_TYPE_ID_V="Retail";
	public final static String CHANNEL_ID_V="WEB";
	public final static String WEBSITE_V="WEB_STAGING";
	public final static String PAYTM_URL="https://securegw-stage.paytm.in/theia/processTransaction"; 
	  //https://securegw-stage.paytm.in/theia/processTransaction
	
	// request param
	public final static String ORDER_ID= "ORDER_ID";
	public final static String CUST_ID= "CUST_ID";
	public final static String TXN_AMOUNT= "TXN_AMOUNT";
	public final static String MOBILE= "MOBILE_NO";
	public final static String EMAIL= "EMAIL";
	public final static String CHECKSUMHASH = "CHECKSUMHASH";
	public static final String TX_STATUS_NEW = "NEW";
	
	//reponse
	public final static String TXNAMOUNT= "TXNAMOUNT";
	public final static String CURRENCY= "CURRENCY";
	public final static String STATUS= "STATUS";
	public final static String RESPCODE= "RESPCODE";
	public final static String RESPMSG= "RESPMSG";
	public final static String BANKTXNID= "BANKTXNID";
	public final static String RESP_ORDERID= "ORDERID";
	public final static String TXNID= "TXNID";
	public final static String TXNDATE= "TXNDATE";
	public final static String TXN_TYPE= "TXNTYPE";
	public final static String GATEWAYNAME= "GATEWAYNAME";
	public final static String BANKNAME= "BANKNAME";
	public final static String PAYMENTMODE= "PAYMENTMODE";
	public final static String REFUNDAMT= "REFUNDAMT";
	
	
	
	private static Map<String, String> paytmMap ;
	
	static {
		paytmMap = new TreeMap<>();
		paytmMap.put("MID",PaytmConfig.MID_V);
		paytmMap.put("CHANNEL_ID",PaytmConfig.CHANNEL_ID_V);
		paytmMap.put("INDUSTRY_TYPE_ID",PaytmConfig.INDUSTRY_TYPE_ID_V);
		paytmMap.put("WEBSITE",WEBSITE_V);
		paytmMap.put("REQUEST_TYPE","DEFAULT");
		
	}
	
	public static TreeMap<String, String> getPaytmParameters(){
		TreeMap<String, String> reqmap = new TreeMap<>();
		reqmap.putAll(paytmMap);
		reqmap.put("MOBILE_NO","7777777777");
		reqmap.put("EMAIL","test@gmail.com");
	//	reqmap.put("CALLBACK_URL", "http://localhost:8080/paytm_java/pgResponse.jsp");
		
		reqmap.put(ORDER_ID, "ORDER101");
		reqmap.put(CUST_ID, "coinxcust");
		reqmap.put(TXN_AMOUNT, "1.00");
		return reqmap;
	}

	public static void main(String[] args) throws Exception {
		CheckSumServiceHelper checksumHelper = CheckSumServiceHelper.getCheckSumServiceHelper();
		String checkSum =  checksumHelper.genrateCheckSum(PaytmConfig.MERCHANT_KEY_V, getPaytmParameters());
		System.out.println("cksum");
		System.out.println(checkSum);
		System.out.println(getPaytmParameters());
		
	}
}

