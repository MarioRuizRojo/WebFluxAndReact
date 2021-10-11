package com.springboot.webflux.restapi.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.restapi.app.handler.ProductHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

import org.springframework.beans.factory.annotation.Value;

/**
 * 
 * @author Mario Ruiz Rojo
 * <br/>
 * REST service Routing Configuration
 * <br/>
 * All the REST service functions are explained in ProductHandler
 * Endpoints:
 * 	List products 					- GET  		/api/product/
 * 	List categories 				- GET  		/api/product/categories
 *	Add new product 				- POST 		/api/product/ 		with JSON product
 *  Update product					- PUT  		/api/product/id 	with JSON product
 *  Delete product					- DELETE  	/api/product/id 	with JSON product
 *  Upload image					- POST  	/api/product/id 	with JSON product
 *  Upload image and create	product - POST  	/api/product/id 	with JSON product    
 */
@Configuration
public class RouterFunctionConfig {	
	@Value("${config.urlProducts}")
	private String urlProducts;
	
	@Value("${config.urlCategories}")
	private String urlCategories;
	
	@Value("${config.paramProductId}")
	private String paramProductId;

	@Bean
	public RouterFunction<ServerResponse> routes(ProductHandler productHandler){
		return route(GET(urlProducts),productHandler::list)
				.andRoute(GET(urlProducts+urlCategories), productHandler::categories)
				.andRoute(GET(urlProducts+"/{"+paramProductId+"}"), productHandler::details)
				.andRoute(POST(urlProducts).and(contentType(MediaType.APPLICATION_JSON)), productHandler::create)
				.andRoute(PUT(urlProducts+"/{"+paramProductId+"}").and(contentType(MediaType.APPLICATION_JSON)), productHandler::edit)
				.andRoute(DELETE(urlProducts+"/{"+paramProductId+"}"), productHandler::delete)
				.andRoute(POST(urlProducts+"/upload/{"+paramProductId+"}").and(contentType(MediaType.MULTIPART_FORM_DATA)), productHandler::upload)
				.andRoute(POST(urlProducts+"/create").and(contentType(MediaType.APPLICATION_JSON)), productHandler::createWithPicture);
	}
	
}
