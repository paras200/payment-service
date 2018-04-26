package com.coinxlab.payment.repos;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.coinxlab.payment.model.PaymentDetails;

public interface PaymentRepository extends CrudRepository<PaymentDetails, Long> {

	List<PaymentDetails> findBySourceUserId(String sourceUserId );
	List<PaymentDetails> findByDestUserId(String destUserId);
	
	//List<PaymentDetails> findBySourceUserIdOrDestUserId(@Param("userId") String userId);
	
    @Query("SELECT pd FROM PaymentDetails pd WHERE pd.sourceUserId=:userId or pd.destUserId=:userId")
	List<PaymentDetails> findAllTxsByUserId(@Param("userId") String userId);
}
