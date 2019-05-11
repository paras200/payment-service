package com.coinxlab.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.coinxlab.payment.model.DirectDeposit;

@EnableAsync
@Component
public class FCMNotification {

	@Value("${email.admins}")
	private String adminEmails;
	
	@Value("${mybuddy.server.url}")
	private String mybuddyServerUrl;
	
	private static Log log = LogFactory.getLog(FCMNotification.class.getName());
	
	private String adminUrl;
	private String notificationUrl;
	
	@PostConstruct
	public void init(){
		adminUrl = mybuddyServerUrl+"/notification/send-notification-to-admins";
		notificationUrl= mybuddyServerUrl+"/notification/send-notification";
		
	}
	
	private void sendAdminNotification(NotificationRequest notificationRequest ) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(adminUrl, notificationRequest, String.class);
	}
	
	private void sendNotification(NotificationRequest notificationRequest ) {
        RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(notificationUrl, notificationRequest, String.class);
	}
	
	@Async
	public void sendDirectDepositRequest(DirectDeposit directDeposit) {
		try {
	        NotificationRequest eb = new NotificationRequest();
	        eb.setBody("Direct Deposit request came from user: " + directDeposit.getUserId() + "  for cash amount :" + directDeposit.getAmount() + "  & credit : " + directDeposit.getCredit() );
	        eb.setTitle("Deposit Request Received");
	        sendAdminNotification(eb);		
		}catch(Exception ex) {
			log.error("Error sending firebase notification via mybuddy service ", ex);
		}
	}
	
	@Async
	public void sendDirectDepositConfirmation(DirectDeposit directDeposit) {
		try {
	        NotificationRequest eb = new NotificationRequest();
	        eb.setBody("Your deposit request is actioned, please chcek your credit balance on app");
	        eb.setTitle("Credit Deposit is confirmed");
	        eb.setUserId(directDeposit.getUserId());
	        sendNotification(eb);
	      }catch(Exception ex) {
			log.error("Error sending firebase notification via mybuddy service ", ex);
		}
	}
	
	private List<String> getAdminEmails() {
		List<String> emailList = new ArrayList<>();
		if(adminEmails != null) {
			String[] adEmails = adminEmails.split(",");
			emailList.addAll(Arrays.asList(adEmails));
		}
		return emailList;
	}
}
