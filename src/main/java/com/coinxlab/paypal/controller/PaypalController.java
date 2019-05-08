package com.coinxlab.paypal.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coinxlab.common.Result;
import com.coinxlab.email.EmailClient;
import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.CcyTxDetail;
import com.coinxlab.payment.repos.CcyTransactionRepository;
import com.coinxlab.payment.service.PaymentProcessor;
import com.coinxlab.paypal.config.PaypalPaymentIntent;
import com.coinxlab.paypal.config.PaypalPaymentMethod;
import com.coinxlab.paypal.model.ResponseData;
import com.coinxlab.paypal.service.PaypalService;
import com.coinxlab.paypal.util.PaypalStatus;
import com.coinxlab.paypal.util.URLUtils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping(path="/paypal")
public class PaypalController {
	
	public static final String PAYPAL_SUCCESS_URL = "pay/success";
	public static final String PAYPAL_CANCEL_URL = "pay/cancel";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaypalService paypalService;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@Autowired
	private CcyTransactionRepository ccyTxRepo;
	
	@Autowired
	private EmailClient emailClient;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index(){
		return "index";
	}
	
	@PostMapping(path="/tx-details") 
	public synchronized @ResponseBody Result deposit (@RequestBody String txdetailsText ) throws PaymentException {	
		log.info("paypal tx to be written   : " + txdetailsText);
		CcyTxDetail ccyTxDeatil = new CcyTxDetail();

		JSONParser parser = new JSONParser();
		JSONObject txdetails;
		try {
			txdetails = (JSONObject)parser.parse(txdetailsText);
		} catch (ParseException e) {
			log.error("Can't parse input request ", e);
			return new Result(Result.STATUS_FAIL);
		}
		
		String id = (String) txdetails.get("id");
        String state = (String) txdetails.get("state");
        
        String userId = (String) txdetails.get("userId");
        String userEmail = (String) txdetails.get("userEmail");
        String credit = (String) txdetails.get("credit");
        Double creditValue = 0.0;
        if(credit != null) {
        	 creditValue = Double.valueOf(credit);
        }
        
        
        ccyTxDeatil.setUserId(userId);
        ccyTxDeatil.setCreditAmount(creditValue);
        
        ccyTxDeatil.setTxId(id);
        ccyTxDeatil.setStatus(state);
        ccyTxDeatil.setPaymentSystem(CcyTxDetail.SYSTEM_PAYPAL);
        ccyTxDeatil.setTxReference(id);
        
        //Reading the array
        //JSONArray countries = (JSONArray)jsonObject.get("payer");
        JSONObject payerobj = (JSONObject)txdetails.get("payer");
     //   JSONObject payerInfo = (JSONObject)payerobj.get("payer_info");
        
        ccyTxDeatil.setPaypalUserEmail((String)payerobj.get("email"));
        
        //Printing all the values
        log.info("id: " + id);
        log.info("state: " + state);
        log.info("payerobj:" + payerobj);
        JSONArray transactions = (JSONArray)txdetails.get("transactions");
        for (Object tx : transactions) {
        	JSONObject  jsonamt = (JSONObject) tx;
        	JSONObject amt = (JSONObject)jsonamt.get("amount");
        	log.info(amt.toJSONString());
        	String total = (String) amt.get("total");
            String currency = (String) amt.get("currency");
            
            log.info(total + " in ccy " + currency);
            ccyTxDeatil.setTxAmount(Double.valueOf(total));
			ccyTxDeatil.setTxCCY(currency);
		}
        
        String fileName = paypalService.writeTxFileToDisc(txdetails.toJSONString(), id);
        ccyTxDeatil.setFileRefernece(fileName);
        ccyTxDeatil = ccyTxRepo.save(ccyTxDeatil);
        
        log.info("Paypal transaction details are saved");
        // make deposit if status is approved
        if(PaypalStatus.approved.name().equalsIgnoreCase(state) || PaypalStatus.completed.name().equalsIgnoreCase(state)) {
        	//validate required paratements
        	if(creditValue <=0.01 || userEmail == null || userId == null) {
        		//TODO don't throw error as paypal tx is success.
        		// just send notification admins, for now error is good for ease of testing
        		emailClient.sendInternalError("Credit deposit failed , key input is missing. " + "credit , userEmail & userId is mandatory :  userId =" + userId + "  credit  = " + credit);
        		throw new PaymentException("credit , userEmail & userId is mandatory :  userId =" + userId + "  credit  = " + credit);
        	}
        	log.info("save of credit deposit is in progress...");
        	emailClient.sendPaymentConfirmation(creditValue, userEmail);
        	paymentProcessor.deposit(userId, userEmail, creditValue);
        }else {
        	emailClient.sendInternalError("Paypal payment issue, status is : " + state);
        }
        
       
		//paymentProcessor.deposit(userId, userEmail, amount);
		log.info("paypal tx completed  for txId : " +id );
		return new Result(Result.STATUS_SUCCESS);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "pay")
	public ResponseData pay(HttpServletRequest request,@RequestParam Double amount ,@RequestParam String ccy ,@RequestParam String desc ){
		String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
		try {
			Payment payment = paypalService.createPayment(
					amount, 
					ccy, 
					PaypalPaymentMethod.paypal, 
					PaypalPaymentIntent.sale,
					desc, 
					cancelUrl, 
					successUrl);
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
					return new ResponseData(links.getHref());
					//return "redirect:" + links.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return new ResponseData("/");
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_CANCEL_URL)
	public String cancelPay(){
		return "cancel";
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_SUCCESS_URL)
	public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
		try {
			Payment payment = paypalService.executePayment(paymentId, payerId);
			if(payment.getState().equals("approved")){
				return "success";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/";
	}
	
}
