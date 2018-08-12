package com.coinxlab.email;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
		templateBasedUrl = emailServerUrl +"/sendMail";
		customEmailUrl = emailServerUrl + "/sendMail"; // TODO change
	}
	
	public void sendPaymentConfirmation(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
	}
	
	public void sendPaymentFailuer(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
	}
	
	public void sendTransactionConfirmation(PaymentDetails pd) {
		try {
			EmailBody emailBody = new EmailBody();
			emailBody.setBody("Credit Transaction of " + pd.getAmount() + " is done from a/c : " + pd.getSourceUserEmail() + "  to a/c : " + pd.getDestUserEmail());
			emailBody.setSubject("Credit Transaction is successfull");
			//emailBody.setTemplate("txn-success");
			List<String> toList = emailBody.getToList();
			toList.add(pd.getSourceUserEmail());
			toList.add(pd.getDestUserEmail());
			emailBody.setToList(toList);
	        RestTemplate restTemplate = new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}
	
	public void sendTransactionFailuer(PaymentDetails pd) {
		try {
			EmailBody emailBody = new EmailBody();
			emailBody.setBody("Credit Transaction of " + pd.getAmount() + " from a/c : " + pd.getSourceUserEmail() + "  to a/c : " + pd.getDestUserEmail() + "  is Failed");
			emailBody.setSubject("Credit Transaction Failuer");
			emailBody.setTemplate("txn-failed");
			List<String> toList = emailBody.getToList();
			toList.add(pd.getSourceUserEmail());
			toList.add(pd.getDestUserEmail());
			emailBody.setToList(toList);
	        RestTemplate restTemplate = new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
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
}
