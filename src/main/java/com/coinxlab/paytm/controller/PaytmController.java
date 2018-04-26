package com.coinxlab.paytm.controller;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.TxDetails;
import com.coinxlab.payment.repos.TransactionRepository;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.payment.service.TxCache;
import com.coinxlab.paytm.PaytmConfig;
import com.coinxlab.paytm.service.PaytmProcessor;

@Controller
@RequestMapping(path="/paytm")
public class PaytmController {
	
	@Autowired
	private PaytmProcessor paytmProcessor;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@Autowired
	private TransactionRepository txRepos;
	
	@PostMapping(path="/generateChecksum")
	public @ResponseBody String generateChecksum( HttpServletRequest request, HttpServletResponse response ) throws PaymentException{
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String[]> mapData = request.getParameterMap();
		TreeMap<String,String> parameters = new TreeMap<String,String>();
		
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			parameters.put(paramName,mapData.get(paramName)[0]);
		}
			
		System.out.println("input parameters : " + parameters);
		 String cksum;
		 try {
			 cksum = paytmProcessor.generateChecksum(parameters);			 
		} catch (Exception e) {			
			e.printStackTrace();
			throw new PaymentException("Error generating checksum ..." ,e);
		}
		 // Tx data  
		 TxDetails tx = new TxDetails();
		 tx.setCksumRequest(cksum);
		 tx.setEmail(parameters.get(PaytmConfig.EMAIL));
		 tx.setCustId(parameters.get(PaytmConfig.CUST_ID));
		 tx.setMobile(parameters.get(PaytmConfig.MOBILE));
		 tx.setOrderId(parameters.get(PaytmConfig.ORDER_ID));
		 tx.setTxAmount(parameters.get(PaytmConfig.TXN_AMOUNT));
		 tx.setStatus(PaytmConfig.TX_STATUS_NEW);
		 txRepos.save(tx);
		 		
		 return cksum;
	}
	// not in use
	@PostMapping(path="/getChecksum")
	public @ResponseBody String generateChecksum(@RequestParam String custId ,@RequestParam String mobile ,@RequestParam String email ,
			@RequestParam String orderId , @RequestParam String txAmt, HttpServletRequest request, HttpServletResponse response ) throws PaymentException{
		 TreeMap<String, String> reqParam = PaytmConfig.getPaytmParameters();
		 reqParam.put(PaytmConfig.CUST_ID, custId);
		 reqParam.put(PaytmConfig.MOBILE, mobile);
		 reqParam.put(PaytmConfig.EMAIL, email);
		 reqParam.put(PaytmConfig.ORDER_ID, orderId);
		 reqParam.put(PaytmConfig.TXN_AMOUNT, txAmt);
		 String cksum;
		 try {
			 cksum = paytmProcessor.generateChecksum(reqParam);			 
		} catch (Exception e) {			
			e.printStackTrace();
			throw new PaymentException("Error generating checksum ..." ,e);
		}
		 // Tx data
		 TxDetails tx = new TxDetails();
		 tx.setCksumRequest(cksum);
		 tx.setEmail(email);
		 tx.setCustId(custId);
		 tx.setMobile(mobile);
		 tx.setOrderId(orderId);
		 tx.setTxAmount(txAmt);
		 tx.setStatus(PaytmConfig.TX_STATUS_NEW);
		 txRepos.save(tx);
		 
		 System.out.println("Request dispatching....");
		 RequestDispatcher rd = request.getRequestDispatcher(PaytmConfig.PAYTM_URL);
		 
		 Map<String, String[]> extraParams = new TreeMap<String, String[]>();
		 String[] valueArr = {cksum};
	     extraParams.put(PaytmConfig.CHECKSUMHASH, valueArr);
	     request = new WrappedRequestWithParameter(request, extraParams);
	        
		 //request.se
		 try {
			 System.out.println("Request forwarding....");
			rd.forward(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return cksum;
	}

	@RequestMapping(path="/callback")
	public @ResponseBody String verifyPaytmResponse( HttpServletRequest request, HttpServletResponse response) throws PaymentException {
		
		Enumeration<String> paramNames = request.getParameterNames();
		Map<String, String[]> mapData = request.getParameterMap();
		TreeMap<String,String> parameters = new TreeMap<String,String>();
		
		String paytmChecksum =  "";
		while(paramNames.hasMoreElements()) {
			String paramName = (String)paramNames.nextElement();
			if(paramName.equals(PaytmConfig.CHECKSUMHASH)){
				paytmChecksum = mapData.get(paramName)[0];
			}else{
				parameters.put(paramName,mapData.get(paramName)[0]);
			}
		}
		TxDetails txDetails = new TxDetails();
		txDetails.setBanktxId(parameters.get(PaytmConfig.BANKTXNID));
		txDetails.setCcy(parameters.get(PaytmConfig.CURRENCY));
		txDetails.setCksumResponse(parameters.get(PaytmConfig.CHECKSUMHASH));
		txDetails.setRespcode(parameters.get(PaytmConfig.RESPCODE));
		txDetails.setRespmsg(parameters.get(PaytmConfig.RESPMSG));	
		
		boolean isValideChecksum = false;
		String result="";
		try{
			isValideChecksum = paytmProcessor.verifyChecksum(parameters, paytmChecksum);
			if(isValideChecksum && parameters.containsKey("RESPCODE")){
				if(parameters.get("RESPCODE").equals("01")){
					result = "SUCCESS";
				}else{
					result="Payment Failed.";
				}
			}else{
				result="Checksum mismatched.";
			}
			txDetails.setResult(result);
		}catch(PaymentException e){
			result = "Paytm reponse is not validated : " + e.getMessage();
			throw new PaymentException("Paytm reponse can't get validated ... need attention ", e);
		}finally{
			TxCache.getInstance().add(txDetails);
			paymentProcessor.addToQueue(txDetails);
		}					
		return result;		
	}
	
	@PostMapping(path="/verifyPayment")
	public @ResponseBody TxDetails verifyPayment(@RequestParam String orderId ){
		 TxDetails tx =  TxCache.getInstance().get(orderId);
		 if(tx == null){
		   // lookup in database
			 List<TxDetails> txList = txRepos.findByOrderId(orderId);
			 if(txList.size() == 1){
				 tx = txList.get(0);
			 }
		 }
		 
		 if(tx == null){
			 // still null -potential error in tx recording
			 System.out.println("Internal Error : we will review transaction recording and get back to you");
			 tx = new TxDetails();
			 tx.setStatus("ERROR");
			 tx.setResult("Internal Error : we will review transaction recording and get back to you");
		 }
		 return tx;
	}
}
