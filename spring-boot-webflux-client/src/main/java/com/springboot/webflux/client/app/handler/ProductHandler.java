package com.springboot.webflux.client.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.client.app.models.Product;
import com.springboot.webflux.client.app.models.services.CoinGeckoService;
import com.springboot.webflux.client.app.models.services.MyService;

import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.*;

import java.net.URI;

import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * 
 * @author Mario Ruiz Rojo
 * <br/>
 * Request hanlder for every REST endpoint
 * <br/>
 * All functions to manage Products and view their price converted to dollars
 */
@Component
public class ProductHandler {
	
	@Autowired
	private MyService myService;
	
	@Autowired
	private CoinGeckoService coinGeckoService;
		
	@Value("${config.urlClient}")
	private String urlClient;
	
	@Value("${config.paramProductId}")
	private String paramProductId;
	
	@Value("${config.paramProductFileName}")
	private String paramProductFileName;
	
	/**
	 * It catches all errors of the current stream and creates a REST http response with bad request code
	 * @param throwa is the exception of the stream with the error message
	 * @return server response with bad request code and error message
	 */
	private Mono<ServerResponse> responseErrors(Throwable throwa){
		return Mono.just(throwa).cast(WebClientResponseException.class)
				.flatMap(error -> {
					switch(error.getStatusCode()) {
					case BAD_REQUEST:
						return ServerResponse.badRequest()
								.contentType(MediaType.APPLICATION_JSON)
								.body(fromValue(error.getResponseBodyAsString()));
					case NOT_FOUND:
						return ServerResponse.notFound().build();
					default:
						return Mono.error(error);
					}						
				});
	}
	
	/**
	 * It converts product's price from euros to dollars
	 * It calls to coin gecko REST api to get the exchange rate
	 * @param product with price in euros
	 * @return product stream with price in dollars
	 */
	public Mono<Product> toDollars(Product product){		
		return coinGeckoService.exchangeEURtoUSDrate()
				.flatMap(rate->{
					product.setPrice(product.getPrice()*rate);
					return Mono.just(product);
				});
	}
	
	/**
	 * It returns the list of products as REST json response
	 * It gets the list of products from REST api products
	 * Converts prices to dollars
	 * @return json list of products
	 */
	public Mono<ServerResponse> list(ServerRequest request){
		return myService.findAll()
				.flatMap(this::toDollars)
				.collectList()
				.flatMap(prod->
				ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(prod)))
				.onErrorResume(this::responseErrors);	
	}
	
	/**
	 * It returns the list of categories as REST json response
	 * It gets the list of categories from REST api products
	 * @return json list of categories
	 */
	public Mono<ServerResponse> categories(ServerRequest request){
		return myService.findAllCategories()
				.collectList()
				.flatMap(cat->
				ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(cat)))
				.onErrorResume(this::responseErrors);	
	}
	
	/**
	 * It returns a product as REST json response
	 * It gets the product from REST api products
	 * Converts prices to dollars
	 * The product has id equals to id parameter in REST request
	 * @return json product
	 */
	public Mono<ServerResponse> details(ServerRequest request){
		String id = request.pathVariable(paramProductId);
		return myService.findById(id)
				.flatMap(this::toDollars)
				.flatMap( product -> 
					ServerResponse.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(product)))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(this::responseErrors);				
	}
	
	/**
	 * It sends an adding request to the REST api products
	 * It sends the product in the REST request 
	 * Converts prices to dollars in the response received
	 * It returns the response received as REST json response
	 * @return json product
	 */
	public Mono<ServerResponse> create(ServerRequest request){
		Mono<Product> monoProduct = request.bodyToMono(Product.class);
		return monoProduct.flatMap(
				product1 -> myService.save(product1).flatMap(this::toDollars).flatMap( 
						product2 -> ServerResponse.created(  URI.create( urlClient+product2.getId() )  )
							.contentType(MediaType.APPLICATION_JSON)
							.body(fromValue(product2))
						)			
						.switchIfEmpty( ServerResponse.notFound().build() )
				).onErrorResume(t->responseErrors(t));		
	}
	
	/**
	 * Updates product with id
	 * It sends a PUT REST request to REST api products with the product and the id that are inside this request
	 * Converts prices in the product updated to dollars
	 * returns the product updated
	 * @return json product
	 */
	public Mono<ServerResponse> edit(ServerRequest request){
		Mono<Product> monoProductChanges = request.bodyToMono(Product.class);
		String id = request.pathVariable(paramProductId);
		return monoProductChanges.flatMap(productChanges->myService.update(productChanges,id)
				.map(this::toDollars)
				.flatMap( monoProductRes -> 
						ServerResponse.created(URI.create(urlClient+id))
						.contentType(MediaType.APPLICATION_JSON)
						.body(monoProductRes, Product.class)))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(t->responseErrors(t));				
	}
	
	/**
	 * Deletes product by id
	 * It sends a delete request to REST api products with id equals to the id in this REST request 
	 * @return no content if found
	 */
	public Mono<ServerResponse> delete(ServerRequest request){
		String id = request.pathVariable(paramProductId);
		return myService.delete(id).then( ServerResponse.noContent().build() )
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(this::responseErrors);
	}
	
	/**
	 * It sends the image and the product id inside this request to REST api products
	 * @return json product
	 */
	public Mono<ServerResponse> upload(ServerRequest request){
		String id = request.pathVariable(paramProductId);
		return request.multipartData().map(multipart->multipart.toSingleValueMap().get(paramProductFileName))
				.cast(FilePart.class)
				.flatMap(filePart -> myService.upload(filePart, id)	)	
				.flatMap(this::toDollars)
				.flatMap( product2 -> 
					ServerResponse.created(URI.create(urlClient+product2.getId()))
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(product2)))							
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(this::responseErrors);
	}	
}
