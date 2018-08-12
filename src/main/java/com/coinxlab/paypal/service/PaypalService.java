package com.coinxlab.paypal.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coinxlab.email.EmailClient;
import com.coinxlab.filemgmt.FileHandlerUtil;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.paypal.config.PaypalPaymentIntent;
import com.coinxlab.paypal.config.PaypalPaymentMethod;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Service
public class PaypalService {

	private Logger log = LoggerFactory.getLogger(getClass());
	private static String PAYPAL_TX_DIR = "txdata" + File.separator + "paypal"; 
	@Autowired
	private APIContext apiContext;
	
	@Autowired
	private EmailClient emailClient;
	
	public Payment createPayment(
			Double total, 
			String currency, 
			PaypalPaymentMethod method, 
			PaypalPaymentIntent intent, 
			String description, 
			String cancelUrl, 
			String successUrl) throws PayPalRESTException{
		Amount amount = new Amount();
		amount.setCurrency(currency);
		amount.setTotal(String.format("%.2f", total));

		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod(method.toString());

		Payment payment = new Payment();
		payment.setIntent(intent.toString());
		payment.setPayer(payer);
		payment.setTransactions(transactions);
		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl(cancelUrl);
		redirectUrls.setReturnUrl(successUrl);
		payment.setRedirectUrls(redirectUrls);

		return payment.create(apiContext);
	}
	
	public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecute = new PaymentExecution();
		paymentExecute.setPayerId(payerId);
		return payment.execute(apiContext, paymentExecute);
	}

	public String writeTxFileToDisc(String text, String txId) throws PaymentException {
		
		String fileName = getFileName(txId);
		try {
			new FileHandlerUtil().writeFile(text, fileName);
		} catch (IOException e) {
			log.error("error in writing file : " + fileName  + " /n data :" + text , e);
			//throw new PaymentException("error writeing tx details to file", e);
			emailClient.sendInternalError("Error writing paypal tx file to disc " + e.getMessage());
		}
		return fileName;
	}

	private String getFileName(String txId) {
		String currentDir = System.getProperty("user.dir");
		String filePath = currentDir + File.separator + PAYPAL_TX_DIR + File.separator ;
		String fileName = txId + "-" + Calendar.getInstance().getTimeInMillis();
		return filePath + fileName;
	}

	
}
