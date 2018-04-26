package com.coinxlab.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.AccountDetails;
import com.coinxlab.payment.model.PaymentDetails;
import com.coinxlab.payment.repos.PaymentRepository;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.payment.utils.TransactionType;


@Controller
@RequestMapping(path="/account")
public class PaymentController {
	
	@Autowired
	private PaymentRepository paymentRepos;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@PostMapping(path="/addTransaction") 
	public @ResponseBody String addTransaction (@RequestParam String srcUserId , @RequestParam String srcUserEmail, @RequestParam String destUserId
			, @RequestParam String destUserEmail, @RequestParam Double amount) throws PaymentException {		
		PaymentDetails pd = new PaymentDetails();
		pd.setSourceUserId(srcUserId);
		pd.setSourceUserEmail(srcUserEmail);
		pd.setDestUserId(destUserId);
		pd.setDestUserEmail(destUserEmail);
		pd.setAmount(amount);
		pd.setTxType(TransactionType.TRANSFER.name());
		pd = paymentProcessor.addTransactions(pd);
		return "Saved";
	}
	
	@GetMapping(path="/allTx")
	public @ResponseBody Iterable<PaymentDetails> getAllTransactions() {
		// This returns a JSON or XML with the users
		return paymentRepos.findAll();
	}
	
	@GetMapping(path="/allTxByUserId")
	public @ResponseBody Iterable<PaymentDetails> getAllTransactionsByUserId(@RequestParam String userId) {
		// This returns a JSON or XML with the users
		return paymentRepos.findAllTxsByUserId(userId);
	}

	
	@PostMapping(path="/deposit") 
	public synchronized @ResponseBody String deposit (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount) throws PaymentException {		
		paymentProcessor.deposit(userId, userEmail, amount);
		return "Saved";
	}
	
	@GetMapping(path="/accountBal")
	public @ResponseBody AccountDetails getAccountBalance(@RequestParam String userId) throws PaymentException {
		AccountDetails ad = paymentProcessor.getAccountDeatils(userId);
		return ad;
	}
	
	@PostMapping(path="/withdraw") 
	public synchronized @ResponseBody String withdraw (@RequestParam String userId , @RequestParam String userEmail, @RequestParam Double amount) throws PaymentException {		
		paymentProcessor.withdraw(userId, userEmail, amount);
		return "Saved";
	}	
}
