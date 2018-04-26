package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.coinxlab.payment.model.AccountDetails;

public interface AccountRepository extends CrudRepository<AccountDetails, Long> {

	List<AccountDetails> findByUserId(String userId );
}
