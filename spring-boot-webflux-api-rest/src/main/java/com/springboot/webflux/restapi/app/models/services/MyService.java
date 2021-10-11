package com.springboot.webflux.restapi.app.models.services;

import com.springboot.webflux.restapi.app.models.documents.Category;
import com.springboot.webflux.restapi.app.models.documents.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Mario Ruiz Rojo
 * Bussines layer interface for managing database
 */
public interface MyService {
	/**
	 * SELECT * FROM products
	 */
	public Flux<Product> findAll();
	/**
	 * SELECT * FROM products
	 * then set names to uppercase
	 */
	public Flux<Product> findAllWithNameUpperCase();
	/**
	 * SELECT * FROM products
	 * then set names to uppercase
	 * then multiply the list by 5000
	 */
	public Flux<Product> findAllWithNameUpperCaseRepeat();
	/**
	 * SELECT * FROM products WHERE products.id = id
	 * @param id identifier of product
	 */
	public Mono<Product> findById(String id);
	/**
	 * INSERT into products
	 * @param product to save
	 */
	public Mono<Product> save(Product product);
	/**
	 * DELETE
	 * @param product to delete
	 */
	public Mono<Void> delete(Product product);
	/**
	 * SELECT * FROM categories
	 */
	public Flux<Category> findAllCategory();
	/**
	 * SELECT * FROM categories WHERE categories.id = id
	 */
	public Mono<Category> findCategoryById(String id);
	/**
	 * INSERT into categories
	 * @param category to save
	 */
	public Mono<Category> saveCategory(Category category);
}
