package com.springboot.webflux.client.app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.springboot.webflux.client.app.handler.ProductHandler;
import com.springboot.webflux.client.app.models.Category;
import com.springboot.webflux.client.app.models.Product;
import com.springboot.webflux.client.app.models.services.CoinGeckoService;
import com.springboot.webflux.client.app.models.services.MyService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.MOCK)//simulated server
class SpringBootWebfluxApiRestApplicationTests {

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
	
	@MockBean
	private MyService myService;
	
	@MockBean
	private CoinGeckoService coinGeckoService;
	
	@InjectMocks
	private ProductHandler productHandler;
	
	private final double rateTest = 1.5;
	private Double expectedPrice;
	
	private List<Product> generateDefaultListTest(){
		List<Product> list = new ArrayList<Product>();
		Category electronics = new Category("electronics");
		Category sports = new Category("sports");
		Category furniture = new Category("furniture");
		Category computers = new Category("computers");
		Product prod = new Product("TV Panasonic Screen LCD", 456.89, electronics);
		list.add(prod);
		prod = new Product("Sony Camera HD Digital", 177.89, electronics);
		list.add(prod);
		prod = new Product("Apple iPod", 46.89, electronics);
		list.add(prod);
		prod = new Product("Sony Notebook", 846.89, computers);
		list.add(prod);
		prod = new Product("Hewlett Packard Multifuncional", 200.89, computers);
		list.add(prod);
		prod = new Product("Bianchi bicycle", 70.89, sports);
		list.add(prod);
		prod = new Product("HP Notebook Omen 17", 2500.89, computers);
		list.add(prod);
		prod = new Product("Rodulf Ikea Desk Table", 150.89,furniture);
		list.add(prod);
		prod = new Product("TV Sony Bravia OLED 4K Ultra HD", 2255.89, electronics);	
		list.add(prod);
		return list;
	}
	
	private Product generateDefaultProductTest(){
		Category electronics = new Category("electronics");
		Product prod = new Product("TV Panasonic Screen LCD", 456.89, electronics);
		prod.setId("IdTest");
		expectedPrice = prod.getPrice()*rateTest;
		return prod;
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.openMocks(this);
		RouterFunction<ServerResponse> routerFunction = RouterFunctions.route(
				GET(urlProducts), productHandler::list)
			.andRoute(GET(urlProducts+"/{"+paramProductId+"}"), productHandler::details)
			.andRoute(POST(urlProducts).and(contentType(MediaType.APPLICATION_JSON)), productHandler::create)
			.andRoute(PUT(urlProducts+"/{"+paramProductId+"}").and(contentType(MediaType.APPLICATION_JSON)), productHandler::edit)
			.andRoute(DELETE(urlProducts+"/{"+paramProductId+"}"), productHandler::delete)
			.andRoute(POST(urlProducts+"/upload/{"+paramProductId+"}").and(contentType(MediaType.MULTIPART_FORM_DATA)), productHandler::upload);
		client = WebTestClient.bindToRouterFunction(routerFunction).build();
	}
	
	@BeforeAll
	public void setupGecko(){
		when(coinGeckoService.exchangeEURtoUSDrate()).thenReturn(Mono.just(rateTest));
	}
	
	@Test
	void listTest() {
		List<Product> prodsDefaultTest = generateDefaultListTest();
		Flux<Product> fluxProd = Flux.fromIterable(prodsDefaultTest);
		
		when(myService.findAll()).thenReturn(fluxProd);
		
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
			Double expectedPriceL = prodsDefaultTest.get(0).getPrice()*rateTest;
			Double responsedPrice = products.get(0).getPrice();
			assertTrue(expectedPriceL.equals(responsedPrice));
		});
		//.hasSize(9);
	}
	
	@Test
	void detailsTest() {
		Product product = generateDefaultProductTest();
		Mono<Product> monoProd = Mono.just(product);
		
		when(myService.findById(any(String.class))).thenReturn(monoProd);		
				
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
		Product product = generateDefaultProductTest();		
		Mono<Product> monoProd = Mono.just(product);
		
		when(myService.findById(any(String.class))).thenReturn(monoProd);
		when(myService.save(any(Product.class))).thenReturn(monoProd);
		
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
			assertTrue(productRes.getPrice().equals(product.getPrice()));
			assertTrue(productRes.getCategory().getName().equals(product.getCategory().getName()));
			assertTrue(productRes.getPrice().equals(expectedPrice));
		});
	}
	
	@Test
	void editTest() {
		Product product = generateDefaultProductTest();
		Mono<Product> monoProd = Mono.just(product);
		
		when(myService.findById(any(String.class))).thenReturn(monoProd);
		when(myService.save(any(Product.class))).thenReturn(monoProd);
		
		//edit first item in the list
		client.put()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, product.getId()))
		.contentType(MediaType.APPLICATION_JSON)//send json
		.accept(MediaType.APPLICATION_JSON)//receive json
		.body(monoProd, Product.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Product.class)
		.consumeWith(response -> {
			Product productRes = response.getResponseBody();
			assertFalse(productRes.getId().isEmpty());
			assertTrue(productRes.getId().equals(product.getId()));
			assertTrue(productRes.getName().equals(product.getName()));
			assertTrue(productRes.getPrice().equals(product.getPrice()));
			assertTrue(productRes.getCategory().getName().equals(product.getCategory().getName()));
			assertTrue(productRes.getPrice().equals(expectedPrice));
		});
	}
	
	@Test
	void deleteTest() {
		Product product = generateDefaultProductTest();
		Mono<Product> monoProd = Mono.just(product);
		when(myService.findById(any(String.class))).thenReturn(monoProd);
		Mono<Void> monoVoid = Mono.empty();
		when(myService.delete(any(String.class))).thenReturn(monoVoid);
		client.delete()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, "idTest"))
		.exchange()
		.expectStatus().isNoContent()
		.expectBody().isEmpty();
		
		when(myService.findById(any(String.class))).thenReturn( Mono.empty());
		client.delete()
		.uri(urlProducts+"/{"+paramProductId+"}", Collections.singletonMap(paramProductId, ""))
		.exchange()
		.expectStatus().isNotFound();
	}
}
