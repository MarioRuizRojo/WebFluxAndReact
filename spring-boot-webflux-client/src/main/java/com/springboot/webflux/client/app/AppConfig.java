package com.springboot.webflux.client.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 
 * @author Mario Ruiz Rojo
 * 
 * Main Spring App configuration to setup the REST api products client connection
 *
 */
@Component
public class AppConfig {
	
	@Value("${microservice.products.url}")
	private String urlMicroServiceProducts;
	
	@Bean
	public WebClient registerWebClient() {
		return WebClient.create(urlMicroServiceProducts);
	}
}
