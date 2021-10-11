package com.springboot.webflux.client.app.models.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.springboot.webflux.client.app.models.CoinGecko;
import com.springboot.webflux.client.app.models.Rate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Mario Ruiz Rojo
 * REST api coingecko request functions
 *
 */
@Service
public class CoinGeckoServiceImpl implements CoinGeckoService{

	@Value("${microservice.coingecko.url}")
	private String urlMicroServiceCoinGecko;
	
	private Rate eurRate;
	private Rate usdRate;
	private Double exchangeRate;
	
	/**
	 * GET exchange rate list
	 * It extracts eur to dollar exchange rate from the rate list
	 */
	@Override
	public Mono<Double> exchangeEURtoUSDrate() {	
		eurRate = null;
		usdRate = null;
		exchangeRate = 0.0;
		WebClient webClient = WebClient.builder().baseUrl(urlMicroServiceCoinGecko).build();
		return webClient.get()
		.accept(MediaType.APPLICATION_JSON)
		.retrieve()
		.bodyToMono(CoinGecko.class)
		.flatMapMany( coingecko -> Flux.fromIterable(coingecko.getRates().get("eur"), coingecko.getRates().get("usd")) )
		.map(rate -> {
			if(rate.getName().equals("Euro"))
				eurRate = rate;
			if(rate.getName().equals("US Dollar"))
				usdRate = rate;
			if(eurRate!=null && usdRate!=null)
				exchangeRate = usdRate.getValue()/eurRate.getValue();
			return 0;
		})
		.then(Mono.just(exchangeRate))
		.onErrorResume(t->Mono.just(1.5));//default value
		//.collectList()
		//.flatMap(n -> Mono.just(exchangeRate));
		//.filter(rate -> rate.type().equals("fiat"))
		//.filter(rate -> rate.name().equals("Euro") || rate.name().equals("US Dollar") )
	}

}
