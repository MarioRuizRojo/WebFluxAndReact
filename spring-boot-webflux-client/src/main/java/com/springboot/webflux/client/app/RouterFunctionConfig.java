package com.springboot.webflux.client.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.springboot.webflux.client.app.handler.ProductHandler;

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
 * 	List products 					- GET  		/api/client/
 * 	List categories 				- GET  		/api/client/categories
 *	Add new product 				- POST 		/api/client/ 		with JSON product
 *  Update product					- PUT  		/api/client/id 	with JSON product
 *  Delete product					- DELETE  	/api/client/id 	with JSON product
 *  Upload image					- POST  	/api/client/id 	with JSON product
 */
@Configuration
public class RouterFunctionConfig {	
	@Value("${config.urlClient}")
	private String urlClient;
	
	@Value("${config.urlCategories}")
	private String urlCategories;
	
	@Value("${config.paramProductId}")
	private String paramProductId;

	@Bean
	public RouterFunction<ServerResponse> routes(ProductHandler productHandler){
		return route(GET(urlClient),productHandler::list)
				.andRoute(GET(urlClient+urlCategories), productHandler::categories)
				.andRoute(GET(urlClient+"/{"+paramProductId+"}"), productHandler::details)
				.andRoute(POST(urlClient).and(contentType(MediaType.APPLICATION_JSON)), productHandler::create)
				.andRoute(PUT(urlClient+"/{"+paramProductId+"}").and(contentType(MediaType.APPLICATION_JSON)), productHandler::edit)
				.andRoute(DELETE(urlClient+"/{"+paramProductId+"}"), productHandler::delete)
				.andRoute(POST(urlClient+"/upload/{"+paramProductId+"}").and(contentType(MediaType.MULTIPART_FORM_DATA)), productHandler::upload);
	}
	
}
