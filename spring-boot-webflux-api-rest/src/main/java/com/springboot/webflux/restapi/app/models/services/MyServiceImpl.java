package com.springboot.webflux.restapi.app.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.springboot.webflux.restapi.app.models.dao.CategoryDao;
import com.springboot.webflux.restapi.app.models.dao.ProductDao;
import com.springboot.webflux.restapi.app.models.documents.Category;
import com.springboot.webflux.restapi.app.models.documents.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 * @author Mario Ruiz Rojo
 * MongoDB queries
 */
@Service
public class MyServiceImpl implements MyService{

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private CategoryDao categoryDao;
	
	/**
	 * SELECT * FROM products
	 */
	@Override
	public Flux<Product> findAll() {
		return productDao.findAll();
	}

	/**
	 * SELECT * FROM products
	 * then set names to uppercase
	 */
	@Override
	public Flux<Product> findAllWithNameUpperCase() {
		return productDao.findAll().map(product -> {
			product.setName(product.getName().toUpperCase());
			return product;
		});
	}

	/**
	 * SELECT * FROM products
	 * then set names to uppercase
	 * then multiply the list by 5000
	 */
	@Override
	public Flux<Product> findAllWithNameUpperCaseRepeat() {
		return findAllWithNameUpperCase().repeat(5000);
	}

	/**
	 * SELECT * FROM products WHERE products.id = id
	 * @param id identifier of product
	 */
	@Override
	public Mono<Product> findById(String id) {
		return productDao.findById(id);
	}

	/**
	 * INSERT into products
	 * @param product to save
	 */
	@Override
	public Mono<Product> save(Product product) {
		return productDao.save(product);
	}

	/**
	 * DELETE
	 * @param product to delete
	 */
	@Override
	public Mono<Void> delete(Product product) {
		return productDao.delete(product);
	}

	/**
	 * SELECT * FROM categories
	 */
	@Override
	public Flux<Category> findAllCategory() {
		return categoryDao.findAll();
	}

	/**
	 * SELECT * FROM categories WHERE categories.id = id
	 */
	@Override
	public Mono<Category> findCategoryById(String id) {
		return categoryDao.findById(id);
	}

	/**
	 * INSERT into categories
	 * @param category to save
	 */
	@Override
	public Mono<Category> saveCategory(Category category) {
		return categoryDao.save(category);
	}

}
