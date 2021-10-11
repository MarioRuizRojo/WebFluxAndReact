package com.springboot.webflux.client.app.models.services;

import org.springframework.http.codec.multipart.FilePart;

import com.springboot.webflux.client.app.models.Category;
import com.springboot.webflux.client.app.models.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Mario Ruiz Rojo
 * Bussines layer interface for managing REST api products
 */
public interface MyService {
	public Flux<Product> findAll();
	public Flux<Category> findAllCategories();
	public Mono<Product> findById(String id);
	public Mono<Product> save(Product product);
	public Mono<Product> update(Product product, String id);
	public Mono<Void> delete(String id);
	public Mono<Product> upload(FilePart file, String id);
}
