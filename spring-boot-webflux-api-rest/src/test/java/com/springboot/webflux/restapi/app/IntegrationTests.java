package com.springboot.webflux.restapi.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.springboot.webflux.restapi.app.models.documents.Product;
import com.springboot.webflux.restapi.app.models.services.MyService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests {
	
	@Autowired
	private MyService myService;

	@Value("${config.urlProducts}")
	private String urlProducts;
	
	@Value("${config.paramProductId}")
	private String paramProductId;
	
	@Value("${config.paramProductName}")
	private String paramProductName;
	
	@Autowired
	private WebTestClient client;
	
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
		});
		//.hasSize(9);
	}
	
	@Test
	void detailsTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product product = fluxProducts.blockFirst();//it should be blocking in unitary test
		client.get()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, product.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$."+paramProductId).isNotEmpty()
		.jsonPath("$."+paramProductName).isEqualTo(product.getName());
	}

	@Test
	void createTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productAux = fluxProducts.blockFirst();//it should be blocking in unitary test
		
		Product product = new Product("living room table",20.23,productAux.getCategory());//set the same category as first item in the list
		
		client.post()
		.uri(urlProducts)
		.contentType(MediaType.APPLICATION_JSON)//send json
		.accept(MediaType.APPLICATION_JSON)//receive json
		.body(Mono.just(product), Product.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Product.class)
		.consumeWith(response -> {
			Product productRes = response.getResponseBody();
			assertFalse(productRes.getId().isEmpty());
			assertTrue(productRes.getName().equals(product.getName()));
			assertTrue(productRes.getPrice().equals(product.getPrice()));
			assertTrue(productRes.getCategory().getName().equals(productAux.getCategory().getName()));
		});
	}
	
	@Test
	void editarTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productFirst = fluxProducts.blockFirst();//it should be blocking in unitary test
		Product productLast = fluxProducts.blockLast();
		
		Product product = new Product("living room table",25.87,productLast.getCategory());//set the same category as last item in the list
		
		//edit first item in the list
		client.put()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, productFirst.getId()))
		.contentType(MediaType.APPLICATION_JSON)//send json
		.accept(MediaType.APPLICATION_JSON)//receive json
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
			assertTrue(productRes.getPrice().equals(product.getPrice()));
			assertTrue(productRes.getCategory().getName().equals(productLast.getCategory().getName()));
		});
	}
	
	@Test
	void deleteTest() {
		Flux<Product> fluxProducts = myService.findAll();
		Product productFirst = fluxProducts.blockFirst();//it should be blocking in unitary test
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
