package com.springboot.webflux.client.app.models.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import static org.springframework.web.reactive.function.BodyInserters.*;
import org.springframework.web.reactive.function.client.WebClient;

import com.springboot.webflux.client.app.models.Category;
import com.springboot.webflux.client.app.models.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
/**
 * 
 * @author Mario Ruiz Rojo
 * REST api products request functions
 */
@Service
public class MyServiceImpl implements MyService{

	@Value("${config.paramProductId:}")
	private String paramProductId;
	
	@Value("${config.paramProductName}")
	private String paramProductName;
	
	@Value("${config.paramProductPrice}")
	private String paramProductPrice;	
	
	@Value("${config.paramProductFileName}")
	private String paramProductFileName;
	
	@Value("${microservice.products.categories.url}")
	private String urlCategories;
	
	/**
	 * REST api products
	 */
	@Autowired
	private WebClient client;
	
	/**
	 * GET list products
	 */
	@Override
	public Flux<Product> findAll() {
		return client.get()		
		.accept(MediaType.APPLICATION_JSON)
		.retrieve()
		.bodyToFlux(Product.class);
	}
	
	/**
	 * GET list categories
	 */
	@Override
	public Flux<Category> findAllCategories() {
		return client.get()
		.uri(urlCategories)
		.accept(MediaType.APPLICATION_JSON)
		.retrieve()
		.bodyToFlux(Category.class);
	}

	/**
	 * GET with id parameter, details of product
	 */
	@Override
	public Mono<Product> findById(String id) {
		return client.get()
				.uri( "/{"+paramProductId+"}", Collections.singletonMap(paramProductId, id) )
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Product.class);
	}

	/**
	 * POST create save product
	 */
	@Override
	public Mono<Product> save(Product product) {
		return client.post()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(product))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Product.class);
	}

	/**
	 * PUT with id parameter, update product
	 */
	@Override
	public Mono<Product> update(Product product, String id) {
		return client.put()
				.uri( "/{"+paramProductId+"}", Collections.singletonMap(paramProductId, id) )
				.contentType(MediaType.APPLICATION_JSON)
				.body(fromValue(product))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Product.class);
		//equivalence
		//.body(fromValue(product))
		//.syncBody(product)
	}

	/**
	 * DELETE with id parameter
	 */
	@Override
	public Mono<Void> delete(String id) {
		return client.delete()
				.uri( "/{"+paramProductId+"}", Collections.singletonMap(paramProductId, id) )
				.retrieve()
				.bodyToMono(Void.class);
	}

	/**
	 * POST with image file and id, upload image and assign to product
	 */
	@Override
	public Mono<Product> upload(FilePart file, String id) {
		MultipartBodyBuilder parts = new MultipartBodyBuilder();
		parts.asyncPart(paramProductFileName, file.content(), DataBuffer.class)
		.headers(h -> h.setContentDispositionFormData(paramProductFileName, file.filename()));
		return client.post()
				.uri( "/upload/{"+paramProductId+"}", Collections.singletonMap(paramProductId, id) )
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.body(fromValue(parts.build()))
				.retrieve()
				.bodyToMono(Product.class);
	}

}
