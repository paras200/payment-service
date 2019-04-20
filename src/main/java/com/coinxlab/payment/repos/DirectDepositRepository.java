package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.coinxlab.payment.model.DirectDeposit;

public interface DirectDepositRepository extends CrudRepository<DirectDeposit, Long> {

	List<DirectDeposit> findByUserId(String userId);
	
	List<DirectDeposit> findByStatus(String status);
}
