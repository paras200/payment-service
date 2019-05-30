package com.coinxlab.payment.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.common.Result;
import com.coinxlab.email.EmailClient;
import com.coinxlab.fx.FxRateManager;
import com.coinxlab.notification.FCMNotification;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.CashTx;
import com.coinxlab.payment.model.CcyTxDetail;
import com.coinxlab.payment.model.DirectDeposit;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.model.RateCard;
import com.coinxlab.payment.repos.CcyTransactionRepository;
import com.coinxlab.payment.repos.DirectDepositRepository;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.payment.utils.TransactionType;


@Controller
@CrossOrigin(origins = "*")
@RequestMapping(path="/account")
public class PaymentController {
	
	private static Log log = LogFactory.getLog(PaymentController.class.getName());
	
	@Autowired
	private PaymentRepository paymentRepos;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@Autowired
	private CcyTransactionRepository ccyTxRepo;
	
	@Autowired
	private FxRateManager fxRateMgr;
	
	@Autowired
	private EmailClient emailClient;
	
	@Autowired
	private DirectDepositRepository ddRepos;
	
	@Autowired
	private FCMNotification fcmNotification;
	
	
	/*@PostMapping(path="/addTransaction") 
	public @ResponseBody String addTransaction (@RequestParam String srcUserId , @RequestParam String srcUserEmail, @RequestParam String destUserId
			, @RequestParam String destUserEmail, @RequestParam Double amount) throws PaymentException {*/		

	
	

	@GetMapping(path="/allTx")
	public @ResponseBody Iterable<PaymentDetails> getAllTransactions() {
		// This returns a JSON or XML with the users
		return paymentRepos.findAll();
	}
	
	@GetMapping(path="/allTxByUserId")
	public @ResponseBody Iterable<PaymentDetails> getAllTransactionsByUserId(@RequestParam String userId) {
		log.info("allTxByUserId... for userId : " + userId);
		return paymentRepos.findAllTxsByUserId(userId);
	}

	@GetMapping(path="/allccyTx")
	public @ResponseBody Iterable<CcyTxDetail> getAllCcyTransactions() {
		// This returns a JSON or XML with the users
		return ccyTxRepo.findAll();
	}
	
	@GetMapping(path="/all-ccytx-userId")
	public @ResponseBody Iterable<CcyTxDetail> getAllCcyTransactionsByUserId(@RequestParam String userId) {
		log.info("allTxByUserId... for userId : " + userId);
		return ccyTxRepo.findByUserId(userId);
	}
	
	
	
	@GetMapping(path="/accountBal")
	public @ResponseBody AccountDetails getAccountBalance(@RequestParam String userId) throws PaymentException {
		AccountDetails ad = paymentProcessor.getAccountDeatils(userId);
		log.info("Accountbalance for  userId : " + userId + " is returned");
		return ad;
	}
	
	@PostMapping(path="/withdraw") 
	public synchronized @ResponseBody Result withdraw (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount , @RequestParam String ccy) throws PaymentException {		
		Integer txId = paymentProcessor.withdraw(userId, userEmail, amount);
		log.info("withdrawal completed by userId : " + userId);
		emailClient.sendWithdrawalConfirmation(userEmail, amount);
		Double cashAmount = 0.0;// we can apply brokearge and FX charge here , for phase 1, will do that calculation manually
		paymentProcessor.saveCashWithdrawalRequest(userId, amount, cashAmount, ccy, txId);
		return new Result(Result.STATUS_SUCCESS);
	}
	
	@PostMapping(path="/withdraw-confirmation-by-admin") 
	public synchronized @ResponseBody Result withdraw (@RequestParam String userId , @RequestParam Long txId, @RequestParam String comment , @RequestParam String adminUserId) throws PaymentException {		
		paymentProcessor.completeCashWithdrawal(adminUserId, userId, txId, comment);
		return new Result(Result.STATUS_SUCCESS);
	}
	
	@GetMapping(path="/get-cash-withdrawal-list") 
	public synchronized @ResponseBody List<CashTx> withdrawTxList (@RequestParam String userId ) throws PaymentException {		
		
		return paymentProcessor.getAllCashWithdrawalRequest(userId);
	}
	
	@GetMapping(path="/get-all-cashtx-list") 
	public synchronized @ResponseBody List<CashTx> getAllCashTxs (@RequestParam String userId ) throws PaymentException {		
		
		List<CashTx> withdrawalList = paymentProcessor.getAllCashWithdrawalRequest(userId);
		List<CashTx> cashTxList = new ArrayList<>();
		List<CcyTxDetail> depositList =  ccyTxRepo.findByUserId(userId);
		for (CcyTxDetail ccyTxDetail : depositList) {
			CashTx cashTx = new CashTx();
			cashTx.setAdminId(ccyTxDetail.getUpdatedBy());
			cashTx.setCashAmt(ccyTxDetail.getTxAmount());
			cashTx.setCcy(ccyTxDetail.getTxCCY());
			cashTx.setLastUpdatedTimeinMilli(ccyTxDetail.getLastUpdatedAt().getTime());
			cashTx.setCreatedAt(ccyTxDetail.getCreatedAt());
			cashTx.setStatus(ccyTxDetail.getStatus());
			cashTx.setTxType(TransactionType.DEPOSIT.name());
			cashTx.setTxId(ccyTxDetail.getId());
			cashTx.setUserId(ccyTxDetail.getUserId());
			
			cashTxList.add(cashTx);
		}
		cashTxList.addAll(withdrawalList);
		List<CashTx> sortedTxList =  cashTxList.stream().sorted((c1,c2) -> Long.compare(c1.getLastUpdatedTimeinMilli(), c2.getLastUpdatedTimeinMilli())).collect(Collectors.toList());
		return sortedTxList;
	}
	
	
	@GetMapping(path="/credit-rate-card")
	public @ResponseBody RateCard getRateList() {
		// TODO get fxrate from yahoo finance and apply handling cost
		float inrRate = fxRateMgr.getDefaultINRRate();
		try {
			 inrRate = fxRateMgr.getINRRate();
		} catch (PaymentException e) {
			e.printStackTrace();
			log.error("error pulling INR Rates ", e);
			emailClient.sendInternalError("error pulling INR Rates : " + e.getMessage());
			// TODO send notification
		}
		return new RateCard(inrRate);
	}
	
	@PostMapping(path="/save-direct-deposit-txdetails") 
	public @ResponseBody Result deposit (@RequestBody DirectDeposit directDeposit) throws PaymentException {	
		if(directDeposit.getUserId() == null) throw new PaymentException("User Id can't be Null");
		//directDeposit.setStatus(DirectDeposit.STATUS_INPROGRESS);
		CcyTxDetail ccyTxDetail = new CcyTxDetail();
		ccyTxDetail.setCreditAmount(directDeposit.getCredit());
		ccyTxDetail.setStatus(DirectDeposit.STATUS_INPROGRESS);
		ccyTxDetail.setPaymentSystem(CcyTxDetail.SYSTEM_DD);
		ccyTxDetail.setUserEmail(directDeposit.getUserEmail());
		ccyTxDetail.setUserId(directDeposit.getUserId());
		ccyTxDetail.setTotalAmount(directDeposit.getAmount());
		ccyTxDetail.setTxAmount(directDeposit.getAmount());
		ccyTxDetail.setTxCCY(directDeposit.getCcy());
		ccyTxDetail.setTxCharge(directDeposit.getTxFee());
		ccyTxDetail.setTxReference(directDeposit.getTxReference());
		ccyTxDetail.setFxRate(directDeposit.getFxRate());
		ccyTxDetail.setTransactionDate(directDeposit.getTransactionDate());
		
		directDeposit.setStatus(DirectDeposit.STATUS_INPROGRESS);
		//directDeposit = directDepositRepo.save(directDeposit);
		paymentProcessor.saveDirectDepositRequest(directDeposit, ccyTxDetail);
		
		emailClient.sendDirectDepositRequest(directDeposit);
		fcmNotification.sendDirectDepositRequest(directDeposit);
		return new Result("SUCCESS");
	}
	
	@GetMapping(value="/get-direct-deposit-list")
	public @ResponseBody List<DirectDeposit> getDirectDepositTxs(@RequestParam String userId) throws PaymentException {
		List<DirectDeposit> depositList = ddRepos.findByUserId(userId);
		
		log.info("direct deposit tx list for " + userId + " is returned");
		return depositList;
	}
	
	@GetMapping(value="/get-all-direct-deposit")
	public @ResponseBody List<DirectDeposit> getAllDirectDeposits() throws PaymentException {
		List<DirectDeposit> ddList = new ArrayList<>();
		Iterable<DirectDeposit> ddItr = ddRepos.findAll();
		ddItr.forEach(ddList::add);;
		
		return ddList;
	}
	
	@GetMapping(value="/get-inprogress-direct-deposit")
	public @ResponseBody List<DirectDeposit> getDirectDepositsByStatus() throws PaymentException {
		List<DirectDeposit> ddList = new ArrayList<>();
		Iterable<DirectDeposit> ddItr = ddRepos.findByStatus(DirectDeposit.STATUS_INPROGRESS);
		ddItr.forEach(ddList::add);;
		
		return ddList;
	}
	
	
	@PostMapping(path="/confirm-direct-deposit-payment") 
	public synchronized @ResponseBody Result validatedDirectDeposit (@RequestParam Integer id ,  @RequestParam String userId, @RequestParam String loginUserEmail) throws PaymentException {	
		DirectDeposit directDeposit = ddRepos.findById(id);
		
		if(directDeposit == null){
			log.error("id is not corret , id supplied is : " + id);
			throw new PaymentException("id is not corret , id supplied is : " + id);
		}
		CcyTxDetail ddTx = ccyTxRepo.findById(directDeposit.getCcyTxId() );
		if(ddTx != null) {
			if(!ddTx.getUserId().equalsIgnoreCase(userId)) {
				log.info("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
				throw new PaymentException("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
			}
			ddTx.setStatus(DirectDeposit.STATUS_COMPLETED);
			ddTx.setUpdatedBy(loginUserEmail);
			ddTx.setLastUpdatedAt(new Date());
			
			directDeposit.setStatus(DirectDeposit.STATUS_COMPLETED);
			directDeposit.setLastUpdatedAt(new Date());
			directDeposit.setUpdatedBy(loginUserEmail);
			paymentProcessor.validatedDirectDeposit(ddTx, directDeposit);
		}else {
			log.error("id is not corret , id supplied is : " + id);
			throw new PaymentException("ccyTx Id  is not corret , id supplied is : " + id);
		}
		log.info("Transaction is confimed for user : " + userId);
		fcmNotification.sendDirectDepositConfirmation(directDeposit);
		return new Result("SUCCESS");
	}
	
	@PostMapping(path="/reject-direct-deposit-payment") 
	public synchronized @ResponseBody Result rejectDirectDeposit (@RequestParam Integer id ,  @RequestParam String userId, @RequestParam String loginUserEmail) throws PaymentException {	
		DirectDeposit directDeposit = ddRepos.findById(id);
		
		if(directDeposit == null){
			log.error("id is not corret , id supplied is : " + id);
			throw new PaymentException("id is not corret , id supplied is : " + id);
		}
		directDeposit.setStatus(DirectDeposit.STATUS_REJECTED);
		directDeposit.setLastUpdatedAt(new Date());
		directDeposit.setUpdatedBy(loginUserEmail);
		
		ddRepos.save(directDeposit);
		
		
		CcyTxDetail ddTx = ccyTxRepo.findById(directDeposit.getCcyTxId() );
		if(ddTx != null) {
			if(!ddTx.getUserId().equalsIgnoreCase(userId)) {
				log.info("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
				throw new PaymentException("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
			}
			ddTx.setStatus(DirectDeposit.STATUS_REJECTED);
			ddTx.setUpdatedBy(loginUserEmail);
			ddTx.setLastUpdatedAt(new Date());
			ccyTxRepo.save(ddTx);
		
		}else {
			log.error("id is not corret , id supplied is : " + id);
			//throw new PaymentException("ccyTx Id  is not corret , id supplied is : " + id);
		}
		log.info("Transaction is rejected for user : " + userId);
		//fcmNotification.sendDirectDepositConfirmation(directDeposit);
		return new Result("SUCCESS");
	}
	
	@GetMapping(value="/total-credits")
	public Result getTotalCreditExchanged() throws PaymentException {
		/*JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("credit-amount", paymentRepos.getTotalCreditTransfer());
			
		} catch (JSONException e) {
			throw new PaymentException("failed to create credit data. ",e);
		}
		*/
		//return jsonObject.toString();
		Result rs = new Result(paymentRepos.getTotalCreditTransfer()+"");
		return rs;
	}
}
