package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.coinxlab.payment.model.CashTx;

public interface CashTxRepository extends CrudRepository<CashTx, Long> {

	List<CashTx> findByTxType(String txType);


	List<CashTx> findByTxTypeAndUserId(String txType , String userId);
}
