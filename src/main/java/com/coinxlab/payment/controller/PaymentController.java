package com.coinxlab.payment.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.model.RateCard;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.payment.utils.TransactionType;


@Controller
@RequestMapping(path="/account")
public class PaymentController {
	private static Log log = LogFactory.getLog(PaymentController.class.getName());
	@Autowired
	private PaymentRepository paymentRepos;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	/*@PostMapping(path="/addTransaction") 
	public @ResponseBody String addTransaction (@RequestParam String srcUserId , @RequestParam String srcUserEmail, @RequestParam String destUserId
			, @RequestParam String destUserEmail, @RequestParam Double amount) throws PaymentException {*/		

	@PostMapping(path="/addTransaction") 
	public @ResponseBody PaymentDetails addTransaction (@RequestBody PaymentDetails pd) throws PaymentException {
		validate(pd);
		pd.setTxType(TransactionType.TRANSFER.name());
		pd.setPaymentSystem("INTERNAL");
		pd = paymentProcessor.addTransactions(pd);
		log.info("Transfer completed for :" + pd);
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

	
	@PostMapping(path="/deposit") 
	public synchronized @ResponseBody String deposit (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount) throws PaymentException {		
		paymentProcessor.deposit(userId, userEmail, amount);
		log.info("deposit completed by userId : " + userId);
		return "Saved";
	}
	
	@GetMapping(path="/accountBal")
	public @ResponseBody AccountDetails getAccountBalance(@RequestParam String userId) throws PaymentException {
		AccountDetails ad = paymentProcessor.getAccountDeatils(userId);
		log.info("Accountbalance for  userId : " + userId + " is returned");
		return ad;
	}
	
	@PostMapping(path="/withdraw") 
	public synchronized @ResponseBody String withdraw (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount) throws PaymentException {		
		paymentProcessor.withdraw(userId, userEmail, amount);
		log.info("withdrawal completed by userId : " + userId);
		return "Saved";
	}
	

	private void validate(PaymentDetails pd) throws PaymentException {		
		if(StringUtils.isEmpty(pd.getDestUserId()) || StringUtils.isEmpty(pd.getSourceUserId()) || StringUtils.isEmpty(pd.getAmount())){
			log.error("key input are missing ... can't proceed with the transaction for : "+ pd);
			throw new PaymentException("key input are missing ... can't proceed with the transaction");
		}
	}
	
	@GetMapping(path="/credit-rate-card")
	public @ResponseBody List<RateCard> getRateList() {
		// This returns a JSON or XML with the users
		return RateCard.getRateList();
	}
}
