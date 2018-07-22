package com.coinxlab.fx;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FxResponse {

	@JsonProperty("USD_INR") private FxResult fxResult ;

	public FxResult getFxResult() {
		return fxResult;
	}

	public void setFxResult(FxResult fxResult) {
		this.fxResult = fxResult;
	}

	@Override
	public String toString() {
		return "FxResponse [fxResult=" + fxResult + "]";
	} 

}
