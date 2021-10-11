package com.springboot.webflux.client.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.springboot.webflux.client.app.models.Product;
import com.springboot.webflux.client.app.models.services.CoinGeckoService;
import com.springboot.webflux.client.app.models.services.MyService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {
	
	@Autowired
	private MyService myService;
	
	@Autowired
	private CoinGeckoService coinGeckoService;

	@Value("${config.urlProducts}")
	private String urlProducts;
	
	@Value("${config.paramProductId}")
	private String paramProductId;
	
	@Value("${config.paramProductName}")
	private String paramProductName;
	
	@Value("${config.paramProductPrice}")
	private String paramProductPrice;
	
	@Autowired
	private WebTestClient client;
	
	private double rateTest;
	
	@BeforeAll
	public void setup() {
		rateTest = coinGeckoService.exchangeEURtoUSDrate().block();
	}
	
	@Test
	void listTest() {		
		client.get()
		.uri(urlProducts)
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Product.class)
		.consumeWith(response -> {
			List<Product> products = response.getResponseBody();
			assertTrue(products.size()>0);
			Product prodFirst = myService.findAll().blockFirst();
			Double expectedPrice = prodFirst.getPrice()*rateTest;
			assertTrue(products.get(0).getPrice().equals(expectedPrice));
		});
		//.hasSize(9);
	}
	
	@Test
	void detailsTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product product = fluxProducts.blockFirst();
		Double expectedPrice = product.getPrice()*rateTest;
		client.get()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, product.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$."+paramProductId).isNotEmpty()
		.jsonPath("$."+paramProductName).isEqualTo(product.getName())
		.jsonPath("$."+paramProductPrice).isEqualTo(expectedPrice);
	}

	@Test
	void createTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productAux = fluxProducts.blockFirst();
		
		Product product = new Product("living room table",20.23,productAux.getCategory());//set the same category as first item in the list
		Double expectedPrice = product.getPrice()*rateTest;
		
		client.post()
		.uri(urlProducts)
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(product), Product.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Product.class)
		.consumeWith(response -> {
			Product productRes = response.getResponseBody();
			assertFalse(productRes.getId().isEmpty());
			assertTrue(productRes.getName().equals(product.getName()));
			assertTrue(productRes.getPrice().equals(expectedPrice));
			assertTrue(productRes.getCategory().getName().equals(productAux.getCategory().getName()));
		});
	}
	
	@Test
	void editarTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productFirst = fluxProducts.blockFirst();
		Product productLast = fluxProducts.blockLast();
		
		Product product = new Product("living room table",25.87,productLast.getCategory());//set the same category as last item in the list
		Double expectedPrice = product.getPrice()*rateTest;
		
		//edit first item in the list
		client.put()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, productFirst.getId()))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.body(Mono.just(product), Product.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Product.class)
		.consumeWith(response -> {
			Product productRes = response.getResponseBody();
			assertFalse(productRes.getId().isEmpty());
			assertTrue(productRes.getId().equals(productFirst.getId()));
			assertTrue(productRes.getName().equals(product.getName()));
			assertTrue(productRes.getPrice().equals(expectedPrice));
			assertTrue(productRes.getCategory().getName().equals(productLast.getCategory().getName()));
		});
	}
	
	@Test
	void deleteTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productFirst = fluxProducts.blockFirst();
		//it will find this product and delete it
		client.delete()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, productFirst.getId()))
		.exchange()
		.expectStatus().isNoContent()
		.expectBody().isEmpty();
		
		//this product doesnt exist anymore, notfound response
		client.delete()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, productFirst.getId()))
		.exchange()
		.expectStatus().isNotFound()
		.expectBody().isEmpty();
	}
}
