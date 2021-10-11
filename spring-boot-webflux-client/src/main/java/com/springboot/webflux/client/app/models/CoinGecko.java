package com.springboot.webflux.client.app.models;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Mario Ruiz Rojo
 * Object wrapper for JSON mapping
 * CoinGecko = {'rates': {'eur':{name:'Euro',unit:'$',value:1.2,type:'fiat'},{'usd':{name:'US Dollar',unit:'$',value:1.0,type:'fiat'}};
 */
public class CoinGecko {
	/**
	 * Set of JSON rate name and rate info {'eur':{name:'euro',unit:'$',value:1.2,type:'fiat'},...}
	 */
	private Map<String,Rate> rates;

	public CoinGecko() {
		
	}
	
	/**
	 * Get rates from coingecko
	 * @return rates
	 */
	public Map<String,Rate> getRates() {
		return rates;
	}

	/**
	 * Setter
	 * @param rates
	 */
	public void setRates(Map<String,Rate> rates) {
		this.rates = rates;
	}
}
