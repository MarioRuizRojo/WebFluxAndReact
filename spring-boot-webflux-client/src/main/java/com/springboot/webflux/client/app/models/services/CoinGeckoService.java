package com.springboot.webflux.client.app.models.services;

import reactor.core.publisher.Mono;

/**
 * 
 * @author Mario Ruiz Rojo
 * Bussines layer interface for managing REST api for exchange rate info
 */
public interface CoinGeckoService {
	/**
	 * GET exchange rate list
	 * It extracts eur to dollar exchange rate from the rate list
	 */
	public Mono<Double> exchangeEURtoUSDrate();
}
