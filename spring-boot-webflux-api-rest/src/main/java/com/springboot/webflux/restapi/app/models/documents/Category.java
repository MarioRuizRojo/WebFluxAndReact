package com.springboot.webflux.restapi.app.models.documents;

import javax.validation.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author Mario Ruiz Rojo
 * It represents categories collection
 */
@Document(collection="categories")
public class Category {
	/**
	 * Identifier of category
	 */
	@Id
	@NotEmpty
	private String id;
	
	/**
	 * Name of category
	 */
	private String name;
	
	public Category() {

	}
	
	public Category(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return Identifier of category
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id Identifier to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Name of category
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name to set in category
	 */
	public void setName(String name) {
		this.name = name;
	}
}
