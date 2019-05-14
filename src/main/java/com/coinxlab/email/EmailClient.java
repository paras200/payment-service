package com.coinxlab.email;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.coinxlab.common.NumberUtil;
import com.coinxlab.payment.model.DirectDeposit;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.utils.AppConstants;

@EnableAsync
@Component
public class EmailClient {

	private static Log log = LogFactory.getLog(EmailClient.class.getName());

	@Value("${email.server.url}")
	private String emailServerUrl;
	
	private String templateBasedUrl;
	private String customEmailUrl;
	
	@Value("${email.admins}")
	private String adminEmails;
	
	
	@PostConstruct
	public void init(){
		templateBasedUrl = emailServerUrl +"/sendTemplateMail";
		customEmailUrl = emailServerUrl + "/sendMail"; // TODO change
	}
	
	//TDOO uncomment send email
	private void sendEmailByTemplate(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
	}
	
	@Async
	public void sendPaymentFailuer(EmailBody emailBody) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(customEmailUrl, emailBody, String.class);
	}
	
	@Async
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
			
	        RestTemplate restTemplate = getRestTemplate();//new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}

	private RestTemplate getRestTemplate() {
		return new RestTemplate();//RestTemplate(getClientHttpRequestFactory());
	}
	
	@Async
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
			
	        RestTemplate restTemplate = getRestTemplate();//new RestTemplate();
	        log.info("sending email .... "  + emailBody);
			restTemplate.postForEntity(templateBasedUrl, emailBody, String.class);
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}

	}
	
	@Async
	public void sendInternalError(String body) {
		try {
	        RestTemplate restTemplate = new RestTemplate();
	        EmailBody eb = new EmailBody();
	        eb.setBody(body);
	        eb.setSubject("Alert - Internal payment processing error");
	        eb.getToList().addAll(getAdminEmails());
	        
	       // eb.setToList(AppConstants.SYSTEM_EMAIL);
	        log.info("sending email .... "  + eb);
			restTemplate.postForEntity(customEmailUrl, eb, String.class);			
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}
	}
	
	@Async
	public void sendDirectDepositRequest(DirectDeposit directDeposit) {
		try {
	        RestTemplate restTemplate = new RestTemplate();
	        EmailBody eb = new EmailBody();
	        eb.setBody("Direct Deposit request came from user: " + directDeposit.getUserId() + "  for cash amount :" + directDeposit.getAmount() + "  & credit : " + directDeposit.getCredit() + ". \n Please review and approve the request");
	        eb.setSubject("Direct Deposit Request from " + directDeposit.getUserId());
	        eb.getToList().addAll(getAdminEmails());
	       // eb.setToList(AppConstants.SYSTEM_EMAIL);
	        log.info("sending email .... "  + eb);
			restTemplate.postForEntity(customEmailUrl, eb, String.class);			
		}catch(Exception ex) {
			log.error("Error sending email ", ex);
		}
	}

	private List<String> getAdminEmails() {
		List<String> emailList = new ArrayList<>();
		emailList.add(AppConstants.MYBUDDY_EMAIL);
		if(adminEmails != null) {
			String[] adEmails = adminEmails.split(",");
			emailList.addAll(Arrays.asList(adEmails));
		}
		return emailList;
	}

	@Async
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
	
	@Async
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

	@Async
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
	
	//Override timeouts in request factory
	private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory()
	{
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
	                      = new HttpComponentsClientHttpRequestFactory();
	    //Connect timeout
	    clientHttpRequestFactory.setConnectTimeout(1000);
	     
	    //Read timeout
	   // clientHttpRequestFactory.setReadTimeout(10_000);
	    return clientHttpRequestFactory;
	}
}
