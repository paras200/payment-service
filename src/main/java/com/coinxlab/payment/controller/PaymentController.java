package com.coinxlab.payment.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.CashTx;
import com.coinxlab.payment.model.CcyTxDetail;
import com.coinxlab.payment.model.DirectDeposit;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.model.RateCard;
import com.coinxlab.payment.repos.CcyTransactionRepository;
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
	
	
	/*@PostMapping(path="/addTransaction") 
	public @ResponseBody String addTransaction (@RequestParam String srcUserId , @RequestParam String srcUserEmail, @RequestParam String destUserId
			, @RequestParam String destUserEmail, @RequestParam Double amount) throws PaymentException {*/		

	@PostMapping(path="/addTransaction") 
	public @ResponseBody PaymentDetails addTransaction (@RequestBody PaymentDetails pd ) throws PaymentException {
		try {
			validate(pd);
			pd.setTxType(TransactionType.TRANSFER.name());
			pd.setPaymentSystem("INTERNAL");
			pd = paymentProcessor.addTransactions(pd);
			log.info("Transfer completed for :" + pd);
			emailClient.sendTransactionConfirmation(pd);
		}catch(PaymentException ex) {
			log.error("creadit transactions failed : " + pd);
			emailClient.sendTransactionFailuer(pd);
			throw ex;
		}
		
		return pd;
	}
	

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
	
	@PostMapping(path="/deposit") 
	public synchronized @ResponseBody Result deposit (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount) throws PaymentException {		
		paymentProcessor.deposit(userId, userEmail, amount);
		log.info("deposit completed by userId : " + userId);
		return new Result("SUCCESS");
	}
	
	@GetMapping(path="/accountBal")
	public @ResponseBody AccountDetails getAccountBalance(@RequestParam String userId) throws PaymentException {
		AccountDetails ad = paymentProcessor.getAccountDeatils(userId);
		log.info("Accountbalance for  userId : " + userId + " is returned");
		return ad;
	}
	
	@PostMapping(path="/withdraw") 
	public synchronized @ResponseBody Result withdraw (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount , @RequestParam Double cashAmount, @RequestParam String ccy) throws PaymentException {		
		Integer txId = paymentProcessor.withdraw(userId, userEmail, amount);
		log.info("withdrawal completed by userId : " + userId);
		emailClient.sendWithdrawalConfirmation(userEmail, amount);
		paymentProcessor.saveCashWithdrawalRequest(userId, amount, cashAmount, ccy, txId);
		return new Result(Result.STATUS_SUCCESS);
	}
	
	@PostMapping(path="/withdraw-confirmation-by-admin") 
	public synchronized @ResponseBody Result withdraw (@RequestParam String userId , @RequestParam Long txId, @RequestParam String comment , @RequestParam String adminUserId) throws PaymentException {		
		paymentProcessor.completeCashWithdrawal(adminUserId, userId, txId, comment);
		return new Result(Result.STATUS_SUCCESS);
	}
	
	@GetMapping(path="/get-cash-withdrawal-list") 
	public synchronized @ResponseBody List<CashTx> withdraw (@RequestParam String userId ) throws PaymentException {		
		
		return paymentProcessor.getAllCashWithdrawalRequest(userId);
	}
	
	private void validate(PaymentDetails pd) throws PaymentException {		
		if(StringUtils.isEmpty(pd.getDestUserId()) || StringUtils.isEmpty(pd.getSourceUserId()) || StringUtils.isEmpty(pd.getAmount())){
			log.error("key input are missing ... can't proceed with the transaction for : "+ pd);
			throw new PaymentException("key input are missing ... can't proceed with the transaction");
		}
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
		//directDeposit.setStatus(DirectDeposit.STATUS_INPROGRESS);
		CcyTxDetail ccyTxDetail = new CcyTxDetail();
		ccyTxDetail.setCreditAmount(directDeposit.getCredit());
		ccyTxDetail.setStatus(DirectDeposit.STATUS_INPROGRESS);
		ccyTxDetail.setPaymentSystem(CcyTxDetail.SYSTEM_DD);
		ccyTxDetail.setUserEmail(directDeposit.getUserEmail());
		ccyTxDetail.setUserId(directDeposit.getUserId());
		ccyTxDetail.setTotalAmount(directDeposit.getAmount());
		ccyTxDetail.setTxCCY(directDeposit.getCcy());
		ccyTxDetail.setTxCharge(directDeposit.getTxFee());
		ccyTxDetail.setTxReference(directDeposit.getTxReference());
		
		//directDeposit = directDepositRepo.save(directDeposit);
		ccyTxDetail = ccyTxRepo.save(ccyTxDetail);
		log.info("deposit deposit details sent by : " + ccyTxDetail.getUserId() + "   email " + ccyTxDetail.getUserEmail());
		return new Result("SUCCESS");
	}
	
	@GetMapping(path="/get-direct-deposit-list")
	public @ResponseBody List<DirectDeposit> getDirectDepositTxs(@RequestParam String userEmail) throws PaymentException {
		List<DirectDeposit> depositList = new ArrayList<>();
		List<CcyTxDetail> txList = ccyTxRepo.findByUserEmailAndPaymentSystem(userEmail, CcyTxDetail.SYSTEM_DD);
		for (CcyTxDetail ccyTxDetail : txList) {
			DirectDeposit dd = new DirectDeposit();
			dd.setAmount(ccyTxDetail.getTotalAmount());
			dd.setCredit(ccyTxDetail.getCreditAmount());
			dd.setStatus(ccyTxDetail.getStatus());
			dd.setUserEmail(ccyTxDetail.getUserEmail());
			dd.setCcy(ccyTxDetail.getTxCCY());
			dd.setTxFee(ccyTxDetail.getTxCharge());
			dd.setUserId(ccyTxDetail.getUserId());
			dd.setId(ccyTxDetail.getId());
			depositList.add(dd);
		}
		log.info("direct deposit tx list for " + userEmail + " is returned");
		return depositList;
	}
	
	@PostMapping(path="/confirm-direct-deposit-payment") 
	public synchronized @ResponseBody Result validatedDirectDeposit (@RequestParam Long id ,  @RequestParam String userId, @RequestParam String loginUserEmail) throws PaymentException {	
		Optional<CcyTxDetail> txDetailList = ccyTxRepo.findById(id);
		if(txDetailList.isPresent()) {
			CcyTxDetail ddTx =  txDetailList.get();
			if(!ddTx.getUserId().equalsIgnoreCase(userId)) {
				log.info("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
				throw new PaymentException("validation failed ... user id is not matching , userId as per txTable : "+ ddTx.getUserId() + "  & supplied userid is : " + userId);
			}
			ddTx.setStatus(DirectDeposit.STATUS_COMPLETED);
			ddTx.setUpdatedBy(loginUserEmail);
			ddTx.setLastUpdatedAt(new Date());
			paymentProcessor.validatedDirectDeposit(ddTx);
		}else {
			log.error("id is not corret , id supplied is : " + id);
			throw new PaymentException("id is not corret , id supplied is : " + id);
		}
		
		return new Result("SUCCESS");
	}
	
	@GetMapping(value="/total-credits")
	public String getTotalCreditExchanged() throws PaymentException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("credit-amount", paymentRepos.getTotalCreditTransfer());
			
		} catch (JSONException e) {
			throw new PaymentException("failed to create credit data. ",e);
		}
		
		return jsonObject.toString();
	}
}
