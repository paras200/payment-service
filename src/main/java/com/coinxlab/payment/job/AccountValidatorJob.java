package com.coinxlab.payment.job;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.coinxlab.payment.service.AccountValidator;

public class AccountValidatorJob extends Thread {

	private static Log log = LogFactory.getLog(AccountValidatorJob.class.getName());
	
	
	private AccountValidator validator;
	private long timeinMilli;
	public AccountValidatorJob (AccountValidator validator){
		this.validator = validator;
		this.timeinMilli = 6*60*60*1000; // 6 hours
	}
	
	@Override
	public void run() {
		while(true){
			try {
				log.info("Account Validator thread started");
				Thread.sleep(timeinMilli);
				log.info("*********Account Validation Started******");
				List<String> listOfMismtach = validator.validateAccount();
				if(listOfMismtach.size()> 0){
					log.info("Number of mismatch found : " + listOfMismtach.size());	
					log.error("Details of Account mismatches : " + listOfMismtach);
					// TODO send email
				}
			} catch (InterruptedException e) {
				log.info("Validator thread intruppeted", e);
			}catch (Exception e) {
				log.error("Error processing account validation job, skiping expection to keep the thread running", e);
				// TODO send email - required urgent attention
			}			
		}
		
	}
}
