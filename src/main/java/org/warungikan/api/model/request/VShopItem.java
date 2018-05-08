package org.warungikan.api.model.request;

import java.io.Serializable;

public class VShopItem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -858376386147710659L;
	private String name;
	private String description;
	private String url;
	private String price;
	private String shopId;
	
	public VShopItem(){}
	
	public VShopItem(String name, String description, String url, String price) {
		setName(name);
		setDescription(description);
		setUrl(url);
		setPrice(price);
	}
	public VShopItem(String shopId, String name, String description, String url, String price) {
		setName(name);
		setDescription(description);
		setUrl(url);
		setPrice(price);
		setShopId(shopId);
		}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getShopId() {
		return shopId;
	}
	public void setShopId(String shopId) {
		this.shopId = shopId;
	}
	
	

}
