package com.springboot.webflux.restapi.app.models.documents;

import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

//sudo service mongod start
/**
 * 
 * @author Mario Ruiz Rojo
 * It represents products collection
 */
@Document(collection="products")
public class Product {
	/**
	 * Identifier of product
	 */
	@Id
	private String id;
	
	/**
	 * Name of product
	 */
	@NotEmpty
	private String name;
	
	/**
	 * Price of product
	 */
	@NotNull
	private Double price;
	
	/**
	 * Creation date of product
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date createdAt;
	
	/**
	 * Category of product
	 */
	@Valid
	@NotNull
	private Category category;
	
	/**
	 * Picture file name of product
	 */
	private String picture;
	
	public Product() {
		super();
	}
	
	public Product(String name, Double price) {
		super();
		this.name = name;
		this.price = price;
	}
	
	public Product(String name, Double price, Category category) {
		this(name,price);
		this.category = category;
	}
	
	public Product(Product productReceived) {
		this(productReceived.name,productReceived.price,productReceived.category);
		this.picture=productReceived.picture;
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
