package com.springboot.webflux.restapi.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.restapi.app.models.documents.Product;

/**
 * 
 * @author Mario Ruiz Rojo
 * MongoDB adapter to products collection
 */
public interface ProductDao extends ReactiveMongoRepository<Product, String>{

}
