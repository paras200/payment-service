package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.coinxlab.payment.model.TxDetails;

public interface TransactionRepository extends CrudRepository<TxDetails, Long> {
	List<TxDetails> findByOrderId(String orderId);
}


