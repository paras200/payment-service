package com.coinxlab.payment.service;


import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.coinxlab.common.NumberUtil;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.model.TxDetails;
import com.coinxlab.payment.repos.AccountRepository;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.repos.TransactionRepository;
import com.coinxlab.payment.utils.AppConstants;
import com.coinxlab.payment.utils.TransactionType;

@Service
@Scope(scopeName="singleton")
public class PaymentProcessor {
	
	private BlockingQueue<TxDetails> queue = new ArrayBlockingQueue<>(100);
    
	@Autowired
	private PaymentRepository paymentRepos;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TransactionRepository txRepos;
	
	@Autowired
	private PaymentProcessor payProcessor;
	
	@PostConstruct
	public void init(){
		for(int i=1 ; i<=10 ; i++){
			TxValidator validator = new TxValidator(queue,txRepos, payProcessor);
			validator.start();
		}			
	}
	
	@Transactional
	public synchronized PaymentDetails addTransactions(PaymentDetails pd) throws PaymentException{
		// validate account balance
		AccountDetails ad = getAccountDeatils(pd.getSourceUserId());
		if(pd.getTxCharge() == null) pd.setTxCharge(0.0);
		if (ad.getAmount() < pd.getAmount() + pd.getTxCharge()) {
			throw new PaymentException("Account Balance is insufficient for user to make the transaction , minimum balance requered is :" + NumberUtil.roundDouble(pd.getAmount() + pd.getTxCharge()) );
		}

		// spit transactions in 2 parts if txcharge > 0
		// part 1- real amount transfer from source to destination			
		// part 2- transaction charge amount transfer from source to system
		
		pd = paymentRepos.save(pd);
		
		// update source a/c balance
		ad.reduce(pd.getAmount());
		ad.setLastTxId(pd.getId());
		accountRepo.save(ad);
		
		// update destination a/c balance
		AccountDetails destAcc = getAccountDeatils(pd.getDestUserId());
		destAcc.add(pd.getAmount());
		destAcc.setLastTxId(pd.getId());
		accountRepo.save(destAcc);
		
		if(pd.getTxCharge() > 0.1f) {		
			// part 1- transaction charge amount transfer from source to system
			// re user same pd object
			PaymentDetails feePd = pd.copy();
			feePd.setAmount(pd.getTxCharge());
			feePd.setTxCharge(0.0);
			feePd.setTxType(TransactionType.TXCHARGE.name());
			feePd.setDestUserEmail(AppConstants.SYSTEM_EMAIL);
			feePd.setDestUserId(AppConstants.SYSTEM_ID);
			
			feePd = paymentRepos.save(feePd);
			
			// update source a/c balance
			ad.reduce(feePd.getAmount());
			ad.setLastTxId(feePd.getId());
			accountRepo.save(ad);
			
			// update destination a/c balance
			destAcc = getAccountDeatils(feePd.getDestUserId());
			destAcc.add(feePd.getAmount());
			destAcc.setLastTxId(feePd.getId());
			accountRepo.save(destAcc);
			
		}
		
		return pd;
	}
	
	@Transactional
	public synchronized void withdraw(String userId, String userEmail, Double amount ) throws PaymentException{
		//validate balance
		AccountDetails ad = getAccountDeatils(userId, userEmail);
		if (ad.getAmount() < amount) {
			throw new PaymentException("Account Balance is insufficient for user :" + userId);
		}

		PaymentDetails pd = new PaymentDetails();
		pd.setSourceUserId(AppConstants.SYSTEM_ID);
		pd.setSourceUserEmail(AppConstants.SYSTEM_EMAIL);
		pd.setDestUserId(userId);
		pd.setDestUserEmail(userEmail);
		pd.setAmount(amount);
		pd.setTxType(TransactionType.WITHDRAWAL.name());
		pd = paymentRepos.save(pd);

		System.out.println("Trasaction saved .. now update account balance");

		// add deposit amount
		ad.setAmount(ad.getAmount() - amount);
		ad.setLastTxId(pd.getId());
		accountRepo.save(ad);
	}
	
	@Transactional
	public synchronized void deposit(String userId, String userEmail, Double amount ) throws PaymentException{
		PaymentDetails pd = new PaymentDetails();
		pd.setSourceUserId(AppConstants.SYSTEM_ID);
		pd.setSourceUserEmail(AppConstants.SYSTEM_EMAIL);
		pd.setDestUserId(userId);
		pd.setDestUserEmail(userEmail);
		pd.setAmount(amount);
		pd.setTxType(TransactionType.DEPOSIT.name());
		
		pd = paymentRepos.save(pd);
		
		System.out.println("Trasaction saved .. now update account balance");
		
		AccountDetails ad = getAccountDeatils(userId, userEmail);
		// add deposit amount
		ad.setAmount(ad.getAmount() + amount);
		ad.setLastTxId(pd.getId());
		accountRepo.save(ad);
	}
	
	public AccountDetails getAccountDeatils(String userId) throws PaymentException {
		return getAccountDeatils(userId,null);
	}
	
	private AccountDetails getAccountDeatils(String userId, String userEmail) throws PaymentException {
		AccountDetails ad = null;
		List<AccountDetails> adLsit = accountRepo.findByUserId(userId);
		if(adLsit.size() > 1){
			throw new PaymentException("More than one entry found in account details for the same user , userId : " + userId);
		}
		if(adLsit.size() == 1){
			ad = adLsit.get(0);
		}else {// 1st time entry
			ad = new AccountDetails();
			ad.setUserId(userId);
			ad.setAmount(0.0);			
			ad.setEmail(userEmail);
		}
		return ad;
	}
	
	public void addToQueue(TxDetails txDetails){
		this.queue.add(txDetails);
	}
}
