package org.warungikan.api.service;

import java.util.List;

import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;

public interface IShopItemService {

	public List<ShopItem> getAllShopItem();
	public ShopItem createShopItem(String name, String description, String url, String price);
	public ShopItem updateShopItem(String id, String name, String description, String url, String price);
	public ShopItemStock addStock(String shopId, String user_id, Integer amount );
	public List<ShopItemStock> getStockByAgent(String user_id);
	public List<ShopItemStock> getAllStocks();
}
