package com.coinxlab.payment.service;


import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.common.NumberUtil;
import com.coinxlab.common.Result;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.CashTx;
import com.coinxlab.payment.model.CcyTxDetail;
import com.coinxlab.payment.model.DirectDeposit;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.model.TxDetails;
import com.coinxlab.payment.repos.AccountRepository;
import com.coinxlab.payment.repos.CashTxRepository;
import com.coinxlab.payment.repos.CcyTransactionRepository;
import com.coinxlab.payment.repos.DirectDepositRepository;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.repos.TransactionRepository;
import com.coinxlab.payment.utils.AppConstants;
import com.coinxlab.payment.utils.TransactionType;

@Service
@Scope(scopeName="singleton")
public class PaymentProcessor {
	
	private BlockingQueue<TxDetails> queue = new ArrayBlockingQueue<>(100);
	
	private static Log log = LogFactory.getLog(PaymentProcessor.class.getName());
    
	@Autowired
	private PaymentRepository paymentRepos;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TransactionRepository txRepos;
	
	@Autowired
	private PaymentProcessor payProcessor;
	
	@Autowired
	private CcyTransactionRepository ccyTxRepo;
	
	@Autowired
	private CashTxRepository cashTxRepo;
	
	@Autowired
	private DirectDepositRepository ddRepos;
	
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
	public synchronized Integer withdraw(String userId, String userEmail, Double amount ) throws PaymentException{
		//validate balance
		AccountDetails ad = getAccountDeatils(userId, userEmail);
		if (ad.getAmount() < amount) {
			throw new PaymentException("Account Balance is insufficient for user :" + userId);
		}

		PaymentDetails pd = new PaymentDetails();
		pd.setSourceUserId(userId);
		pd.setSourceUserEmail(userEmail);
		pd.setDestUserId(AppConstants.SYSTEM_ID);
		pd.setDestUserEmail(AppConstants.SYSTEM_EMAIL);
		pd.setAmount(amount);
		pd.setTxType(TransactionType.WITHDRAWAL.name());
		pd = paymentRepos.save(pd);

		System.out.println("Trasaction saved .. now update account balance");

		// add balance amount
		ad.setAmount(ad.getAmount() - amount);
		ad.setLastTxId(pd.getId());
		accountRepo.save(ad);
		
		return pd.getId();
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
	
	@Transactional
	public synchronized void validatedDirectDeposit (CcyTxDetail ddTx, DirectDeposit directDeposit) throws PaymentException {	
		//directDeposit = directDepositRepo.save(directDeposit);
		ddRepos.save(directDeposit);
		
		ddTx = ccyTxRepo.save(ddTx);
		log.info("deposit deposit tx validated for : " + ddTx.getUserEmail() + "   tx ref :  " + ddTx.getTxReference());
		log.info("add credits to user deposit");
		try {
			deposit(ddTx.getUserId(), ddTx.getUserEmail(), ddTx.getCreditAmount());
		} catch (PaymentException e) {
			log.error( "direct deposit failed for user : " + ddTx.getUserEmail() + "  and  tx ref :  " + ddTx.getTxReference(), e);
			throw new PaymentException( "direct deposit failed for user : " + ddTx.getUserEmail() + "  and  tx ref :  " + ddTx.getTxReference(), e);
		}
	}
	
	@Transactional
	public DirectDeposit saveDirectDepositRequest (DirectDeposit directDeposit , CcyTxDetail ccyTxDetail) throws PaymentException {	
		ccyTxDetail = ccyTxRepo.save(ccyTxDetail);
		log.info("deposit deposit details sent by : " + ccyTxDetail.getUserId() + "   email " + ccyTxDetail.getUserEmail());
		directDeposit.setCcyTxId(ccyTxDetail.getId());
		
		directDeposit = ddRepos.save(directDeposit);
		log.info("direct deposit requets saved");
		return directDeposit;
	}
	public AccountDetails getAccountDeatils(String userId) throws PaymentException {
		return getAccountDeatils(userId,null);
	}
	
	public void saveCashWithdrawalRequest(String userId, Double creditAmt , Double cashAmt, String ccy, Integer txId) {
		CashTx cashTx = new CashTx();
		cashTx.setCashAmt(cashAmt);
		cashTx.setCreditAmt(creditAmt);
		cashTx.setUserId(userId);
		cashTx.setTxType(TransactionType.WITHDRAWAL.name());
		cashTx.setCcy(ccy);
		cashTx.setTxId(txId);
		cashTxRepo.save(cashTx);
		log.info("Cash withdrawal request has noted " + cashTx);
	}
	
	public void completeCashWithdrawal(String adminId, String userId,Long txId , String comment) throws PaymentException {
		Optional<CashTx> optTx = cashTxRepo.findById(txId);
		if(optTx.isPresent()) {
			CashTx cashTx = optTx.get();
			cashTx.setAdminId(adminId);
			cashTx.setStatus(CashTx.COMPLETED);
			cashTx.setComment(comment);
			cashTx.setLastUpdatedTimeinMilli(Calendar.getInstance().getTimeInMillis());
			if(!(userId.equals(cashTx.getUserId()))) {
				throw new PaymentException("cash txId is invalid, user details not matching");
			}
			cashTxRepo.save(cashTx);
		}else {
			throw new PaymentException("cash txId is invalid, no transcation with this request exits");
		}
		log.info("Cash is transfered to user account by admin : " + adminId + "   for transaction : " + txId);
	}
	
	public List<CashTx> getAllCashWithdrawalRequest(String userId){
		return cashTxRepo.findByTxTypeAndUserId(TransactionType.WITHDRAWAL.name(), userId);
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
