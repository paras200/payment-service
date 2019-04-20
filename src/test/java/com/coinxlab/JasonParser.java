package com.coinxlab;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.coinxlab.common.NumberUtil;
import com.coinxlab.payment.model.RateCard;

public class JasonParser {

	public static void main(String args[]) throws ParseException
    {
		
		System.out.println(NumberUtil.roundDouble(1923.5667));
		JSONParser pas = new JSONParser();
		String response = "{\"USD_INR\":69.407501}";
		JSONObject jObject = (JSONObject)pas.parse(response );
		Double	 value = (Double)jObject.get("USD_INR");
		System.out.println(value);
		
		RateCard rc = new RateCard(100f);
		rc.getFxRates();
        JSONParser parser = new JSONParser();
        try
        {
            Object object = parser.parse(new FileReader("/Users/sinhanil/dev/payment/payment-service/src/main/resources/sample.json"));
            
            //convert Object to JSONObject
            JSONObject jsonObject = (JSONObject)object;
            
            //Reading the String
            String id = (String) jsonObject.get("id");
            String state = (String) jsonObject.get("state");
            
            //Reading the array
            //JSONArray countries = (JSONArray)jsonObject.get("payer");
            JSONObject payerobj = (JSONObject)jsonObject.get("payer");
            
            //Printing all the values
            System.out.println("id: " + id);
            System.out.println("state: " + state);
            System.out.println("payerobj:" + payerobj);
            JSONArray transactions = (JSONArray)jsonObject.get("transactions");
            for (Object tx : transactions) {
            	JSONObject  jsonamt = (JSONObject) tx;
            	JSONObject amt = (JSONObject)jsonamt.get("amount");
            	System.out.println(amt.toJSONString());
            	String total = (String) amt.get("total");
                String currency = (String) amt.get("currency");
                
                System.out.println(total + " in ccy " + currency);
				
			}
          //  JSONObject amt = (JSONObject)transactions.get("amount");
          //  String total = (String) amt.get("total");
            //String currency = (String) amt.get("currency");
            
//            for(Object country : countries)
//            {
//                System.out.println("\t"+country.toString());
//            }
        }
        catch(FileNotFoundException fe)
        {
            fe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
