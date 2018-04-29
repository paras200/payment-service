package com.coinxlab.payment.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.coinxlab.payment.model.TxDetails;

public class TxCache {

	private  Map<String, TxDetails> txCache = new ConcurrentHashMap<>();
	
	private static TxCache tx ; ;
	private TxCache(){
		
	}
	public static synchronized TxCache  getInstance() {
		if(tx == null){
			tx = new TxCache();
		}
		return tx;
	}
	
	public void add(TxDetails txDetails){
		txCache.put(txDetails.getOrderId(), txDetails);
	}
	
	public TxDetails get(String orderId){
		return txCache.get(orderId);
	}
	public void remove(String orderId){
		txCache.remove(orderId);
	}
 }
