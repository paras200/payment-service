package com.coinxlab.payment.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.coinxlab.email.EmailClient;
import com.coinxlab.payment.job.AccountValidatorJob;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.repos.AccountRepository;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.utils.AppConstants;

@Service
@Scope(scopeName="singleton") // This must be singelton
public class AccountValidator {

	private static Log log = LogFactory.getLog(AccountValidator.class.getName());
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private EmailClient emailClient;
		
	@PostConstruct
	public void init(){
		AccountValidatorJob job = new AccountValidatorJob(this);
		job.start();
	}
	
	public List<String> validateAccount(){
		List<String> listOfMsitach = new ArrayList<>();
		Iterable<AccountDetails> accList = getListOfAccounts();
		for (AccountDetails ad : accList) {
			log.info("Validataion started for the a/c : "+ad);
			Double origAccBalance = ad.getAmount();
			Double derivedAccBal = getAccoutBalanceAsPerPaymentData(ad.getUserId());
			if((Math.abs(origAccBalance - derivedAccBal)) <= 0.01){
				log.info("balance matching for user a/c, userID : " + ad.getUserId());
			}else{
				log.error("balance ** NOT ** matching for user a/c, userID : " + ad.getUserId());
				log.error("origAccBalance : "+ origAccBalance + "   &   derivedAccBal : "+ derivedAccBal );
				StringBuilder txt =new StringBuilder( "acc details : " + ad.getUserId() +" - " + ad.getEmail() + "\n");
				txt.append("  origAccBalance : " + origAccBalance + "\n");
				txt.append("  derivedAccBal : " + derivedAccBal + "\n");
				listOfMsitach.add(txt.toString());
			}
		}
		// send email 
		if(listOfMsitach.size() > 0) {
			emailClient.sendInternalError("Number of mismatch found : " + listOfMsitach.size() + " \n " + listOfMsitach.toString());
		}
		
		return listOfMsitach;		
	}
	
	/*
	 * Right now validating all accounts, but this should change in future and 
	 * it should just focus on valaditing transaction after a given snapshot only
	 * */
	private Iterable<AccountDetails> getListOfAccounts(){
		return accountRepository.findAll();
	}
	
	private Double getAccoutBalanceAsPerPaymentData(String userId){
		List<PaymentDetails>  pdList = paymentRepository.findAllTxsByUserId(userId);
		Double amountReceive = 0.0;
		Double amountPay = 0.0;		
		
		for (PaymentDetails pd : pdList) {
			if(userId.equalsIgnoreCase(AppConstants.SYSTEM_ID) && 
					(AppConstants.DEPOSIT.equals(pd.getTxType()) || AppConstants.WITHDRAWAL.equals(pd.getTxType()))) {
				continue;
			}
			if(userId.equalsIgnoreCase(pd.getDestUserId())){
				amountReceive += pd.getAmount();
			}else if (userId.equalsIgnoreCase(pd.getSourceUserId())){
				amountPay += pd.getAmount();
			}else {
				log.warn("Not expected... userId must match with either source "
						+ "or destination user Id, seems like a data issues which needs attention");
			}
		}
		return amountReceive - amountPay;
	}
}
