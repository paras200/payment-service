package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.coinxlab.payment.model.CcyTxDetail;

public interface CcyTransactionRepository extends CrudRepository<CcyTxDetail, Long> {
	
	List<CcyTxDetail> findByUserId(String userId);
	List<CcyTxDetail> findByUserEmail(String email);
	List<CcyTxDetail> findByUserEmailAndPaymentSystem(String email, String paymentSystem);
	
}
