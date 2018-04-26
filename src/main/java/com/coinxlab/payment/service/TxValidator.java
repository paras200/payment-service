package com.coinxlab.payment.service;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import com.coinxlab.payment.error.PaymentException;
import com.coinxlab.payment.model.TxDetails;
import com.coinxlab.payment.repos.TransactionRepository;

public class TxValidator extends Thread {

	private BlockingQueue<TxDetails> queue;
	//private PaytmProcessor paytmProcessor;
	private TransactionRepository transactionRepository;
	private PaymentProcessor paymentProcessor;
	public TxValidator(BlockingQueue<TxDetails> queue,TransactionRepository transactionRepository,PaymentProcessor paymentProcessor) {
		this.queue = queue;
		this.transactionRepository = transactionRepository;
		this.paymentProcessor = paymentProcessor;
	}
	@Override
	public void run() {
		while(true){
			TxDetails origTx =null;
			try {
				TxDetails txData = queue.take();
				origTx = txData; 
				List<TxDetails> origTxList = transactionRepository.findByOrderId(txData.getOrderId());
				if(origTxList.size() == 1){
					origTx = origTxList.get(0);
					origTx.setBanktxId(txData.getBanktxId());
					origTx.setCcy(txData.getCcy());
					origTx.setCksumResponse(txData.getCksumResponse());
					origTx.setRespcode(txData.getRespcode());
					origTx.setRespmsg(txData.getRespmsg());
					origTx.setResult(txData.getResult());
					origTx.setStatus(txData.getStatus());	
					origTx.setTxAmount(txData.getTxAmount());
				}else{
					System.err.println("Error in processing the transaction record, number of records found with the order id :" + txData.getOrderId()+ "  : is : " +origTxList.size());
					origTx.setStatus("Internal Error");
					origTx.setResult("Error in processing the transaction record, number of records found with the order id :" + txData.getOrderId()+ "  : is : " +origTxList.size());
				}
				origTx.setLastUpdated(Calendar.getInstance().getTime());
				// save paytm response 
				transactionRepository.save(txData);
								
				// Validate transaction using Paytm
				
				// update internal tx table for deposit
				// TODO : Revisit this, so far its assumed payment is always a depost in user account,this may change in future as payment to dubm starts
				paymentProcessor.deposit(origTx.getCustId(), origTx.getEmail(),Double.valueOf(origTx.getTxAmount()));
				
				//TODO send email notification on error
			} catch (InterruptedException e) {				
				e.printStackTrace();
			} catch (NumberFormatException e) {
				System.err.println("error updating deposit for the oder id: " + origTx.getOrderId());
				e.printStackTrace();
			} catch (PaymentException e) {
				System.err.println("error updating deposit for the oder id: " + origTx.getOrderId());
				e.printStackTrace();
			}
		}
		
	}

}
