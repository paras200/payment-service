package com.coinxlab.paypal.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class PaypalTxDetails {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;

	private String userId;
	private String userEmail;
	
	
	
}
