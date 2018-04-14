package id.travel.api.service;

import java.util.List;

import org.warungikan.db.model.ShopItem;

public interface IShopItemService {

	public List<ShopItem> getAllShopItem();
	public void createShopItem(String name, String description, String url, String price);
	public void updateShopItem(String id, String name, String description, String url, String price);
}
