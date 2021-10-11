package com.springboot.webflux.restapi.app.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.springboot.webflux.restapi.app.models.documents.Category;

/**
 * 
 * @author Mario Ruiz Rojo
 * MongoDB adapter to categories collection
 */
public interface CategoryDao extends ReactiveMongoRepository<Category, String>{

}
