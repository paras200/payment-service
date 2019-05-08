package com.coinxlab.payment.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.common.Result;
import com.coinxlab.email.EmailClient;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.payment.utils.TransactionType;

@Controller
@RequestMapping(path="/account-internal")
public class InternalPaymentController {
	
	private static Log log = LogFactory.getLog(InternalPaymentController.class.getName());
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@Autowired
	private EmailClient emailClient;
	
	@PostMapping(path="/addTransaction") 
	public @ResponseBody PaymentDetails addTransaction (@RequestBody PaymentDetails pd ) throws PaymentException {
		try {
			validate(pd);
			pd.setTxType(TransactionType.TRANSFER.name());
			pd.setPaymentSystem("INTERNAL");
			pd = paymentProcessor.addTransactions(pd);
			log.info("Transfer completed for :" + pd);
			emailClient.sendTransactionConfirmation(pd);
		}catch(PaymentException ex) {
			log.error("creadit transactions failed : " + pd);
			emailClient.sendTransactionFailuer(pd);
			throw ex;
		}
		
		return pd;
	}
	
	@PostMapping(path="/deposit") 
	public synchronized @ResponseBody Result deposit (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount , @RequestParam String password) throws PaymentException {	
		if(!(password.equalsIgnoreCase("oceantree"))) {
			throw new PaymentException("not authorized, please contact admin");
		}
		paymentProcessor.deposit(userId, userEmail, amount);
		log.info("deposit completed by userId : " + userId);
		return new Result("SUCCESS");
	}
	
	private void validate(PaymentDetails pd) throws PaymentException {		
		if(StringUtils.isEmpty(pd.getDestUserId()) || StringUtils.isEmpty(pd.getSourceUserId()) || StringUtils.isEmpty(pd.getAmount())){
			log.error("key input are missing ... can't proceed with the transaction for : "+ pd);
			throw new PaymentException("key input are missing ... can't proceed with the transaction");
		}
	}
}
