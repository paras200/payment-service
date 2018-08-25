package com.coinxlab.email;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.coinxlab.common.NumberUtil;
import com.coinxlab.payment.controller.PaymentController;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.utils.AppConstants;


@Component
public class EmailClient {

	private static Log log = LogFactory.getLog(PaymentController.class.getName());

	@Value("${email.server.url}")
	private String emailServerUrl;
	
	private String templateBasedUrl;
	private String customEmailUrl;
	
	
	@PostConstruct
	public void init(){
		templateBasedUrl = emailServerUrl +"/sendTemplateMail";
		customEmailUrl = emailServerUrl + "/sendMail"; // TODO change
	}
	
	public void sendEmailByTemplate(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
	}
	
	public void sendPaymentFailuer(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
	}
	
	public void sendTransactionConfirmation(PaymentDetails pd) {
		try {
			EmailBody emailBody = new EmailBody();
			//emailBody.setBody("Credit Transaction of " + pd.getAmount() + " is done from a/c : " + pd.getSourceUserEmail() + "  to a/c : " + pd.getDestUserEmail());
			//emailBody.setSubject("Credit Transaction is successfull");
			emailBody.setTemplate("txn-success");
			List<String> toList = emailBody.getToList();
			toList.add(pd.getSourceUserEmail());
			toList.add(pd.getDestUserEmail());
			emailBody.setToList(toList);
			
			Map<String,String> paramMap = new HashMap<>();
			paramMap.put("credit", NumberUtil.roundDouble(pd.getAmount()));
			paramMap.put("fromUser", pd.getSourceUserEmail());
			paramMap.put("toUser", pd.getDestUserEmail());
			paramMap.put("txnCharge", NumberUtil.roundDouble(pd.getTxCharge()));
			emailBody.setParamMap(paramMap);
			
	        RestTemplate restTemplate = new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}
	
	public void sendTransactionFailuer(PaymentDetails pd) {
		try {
			EmailBody emailBody = new EmailBody();
			//emailBody.setBody("Credit Transaction of " + pd.getAmount() + " from a/c : " + pd.getSourceUserEmail() + "  to a/c : " + pd.getDestUserEmail() + "  is Failed");
			//emailBody.setSubject("Credit Transaction Failuer");
			emailBody.setTemplate("txn-failed");
			List<String> toList = emailBody.getToList();
			toList.add(pd.getSourceUserEmail());
			toList.add(pd.getDestUserEmail());
			emailBody.setToList(toList);
			
			Map<String,String> paramMap = new HashMap<>();
			paramMap.put("nosOfcredit", NumberUtil.roundDouble(pd.getAmount()));
			paramMap.put("fromUser", pd.getSourceUserEmail());
			paramMap.put("toUser", pd.getDestUserEmail());
			emailBody.setParamMap(paramMap);
			
	        RestTemplate restTemplate = new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}
	
	public void sendInternalError(String body) {
		try {
	        RestTemplate restTemplate = new RestTemplate();
	        EmailBody eb = new EmailBody();
	        eb.setBody(body);
	        eb.setSubject("Alert - Internal payment processing error");
	        eb.getToList().add(AppConstants.MYBUDDY_EMAIL);
	       // eb.setToList(AppConstants.SYSTEM_EMAIL);
	        log.info("sending email .... "  + eb);
			restTemplate.postForEntity(customEmailUrl, eb, String.class);			
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}

	public void sendPaymentConfirmation(Double creditValue, String userEmail) {
		EmailBody eb = new EmailBody();
		eb.setTemplate("add-credit");
		List<String> toList = eb.getToList();
		toList.add(userEmail);
		eb.setToList(toList);
		
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("credit", NumberUtil.roundDouble(creditValue));
		paramMap.put("email", userEmail);
		
		eb.setParamMap(paramMap);
		sendEmailByTemplate(eb);
	}
	
	public void sendPaymentFailuer(Double creditValue, String userEmail) {
		EmailBody eb = new EmailBody();
		eb.setTemplate("add-credit");
		List<String> toList = eb.getToList();
		toList.add(userEmail);
		eb.setToList(toList);
		
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("credit", NumberUtil.roundDouble(creditValue));
		paramMap.put("email", userEmail);
		
		eb.setParamMap(paramMap);
		sendEmailByTemplate(eb);
	}

	public void sendWithdrawalConfirmation(String userEmail, Double amount) {
		EmailBody eb = new EmailBody();
		eb.setTemplate("withdraw-credit");
		List<String> toList = eb.getToList();
		toList.add(userEmail);
		eb.setToList(toList);
		
		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("credit", amount+"");//NumberUtil.roundDouble(amount)
		paramMap.put("email", userEmail);
		eb.setParamMap(paramMap);
		
		sendEmailByTemplate(eb);
	}
}
