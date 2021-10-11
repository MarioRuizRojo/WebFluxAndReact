package com.springboot.webflux.client.app.models;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author Mario Ruiz Rojo
 * It represents a product
 */
public class Product {
	/**
	 * Identifier of product
	 */
	private String id;
	
	/**
	 * Name of product
	 */
	private String name;
	
	/**
	 * Price of product
	 */
	private Double price;
	
	/**
	 * Creation date of product
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createdAt;
	
	/**
	 * Category of product
	 */
	private Category category;
	
	/**
	 * Picture file name of product
	 */
	private String picture;
	
	public Product() {

	}
	
	public Product(String name, Double price) {
		this.name = name;
		this.price = price;
	}
	
	public Product(String name, Double price, Category category) {
		this(name,price);
		this.category = category;
	}
	/**
	 * 
	 * @return Identifier of product
	 */
	public String getId() {
		return id;
	}
	/**
	 * 
	 * @return id to set Identifier of product
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * 
	 * @return Name of product
	 */
	public String getName() {
		return name;
	}
	/**
	 * 
	 * @param name to set Name of product
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return Price of product
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * 
	 * @param price to set Price of product
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	/**
	 * 
	 * @return Creation date of product
	 */
	public Date getCreatedAt() {
		return createdAt;
	}
	/**
	 * 
	 * @param createdAt set to Creation date of product
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	/**
	 * 
	 * @return Category of product
	 */
	public Category getCategory() {
		return category;
	}
	/**
	 * 
	 * @param category set to Category of product
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	/**
	 * 
	 * @return Picture file name of product
	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * 
	 * @param picture to Picture file name of product
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
}
