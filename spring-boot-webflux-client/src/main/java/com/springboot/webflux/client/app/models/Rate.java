package com.springboot.webflux.client.app.models;

/**
 * 
 * @author Mario Ruiz Rojo
 * CoinGecko Rate Object
 * {name:'euro',unit:'$',value:1.2,type:'fiat'}
 */
public class Rate {
	private String name;
	private String unit;
	private Double value;
	private String type;
	
	public Rate() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
}
