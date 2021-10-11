package com.springboot.webflux.client.app.models;

/**
 * 
 * @author Mario Ruiz Rojo
 * It represents categories collection
 */
public class Category {
	/**
	 * Identifier of category
	 */
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
