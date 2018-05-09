package org.warungikan.api.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.api.service.IShopItemService;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.ShopItemRepository;
import org.warungikan.db.repository.ShopItemStockRepository;
import org.warungikan.db.repository.UserRepository;

@Service
public class ShopItemServiceImpl implements IShopItemService{

	@Autowired
	private ShopItemRepository shopRepository;
	
	@Autowired
	private ShopItemStockRepository stockRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public List<ShopItem> getAllShopItem() {

		return shopRepository.findAll();
	}

	@Override
	public ShopItem createShopItem(String name, String description, String url, String price) {
		ShopItem i = new ShopItem();
		i.setCreationDate(new Date());
		return shopRepository.save(setValue(i, name, description, url, price));
	}

	@Override
	public ShopItem updateShopItem(String id, String name, String description, String url, String price) {
		ShopItem i = shopRepository.findOne(Long.parseLong(id));
		i.setLastModifiedDate(new Date());
		return shopRepository.save(setValue(i, name, description, url, price));
	}
	
	private ShopItem setValue(ShopItem i, String name, String description, String url, String price){
		i.setDescription(description);
		i.setName(name);
		i.setPrice(Long.parseLong(price));
		i.setUrl(url);
		return i;
	}

	@Override
	public List<ShopItemStock> getStockByAgent(String user_id) {
		User agent = userRepository.findUserByUserId(user_id);
		List<ShopItemStock> stocks = shopRepository.findStockByAgent(agent);
		return stocks;
	}

	@Override
	public ShopItemStock addStock(String shopId, String user_id, Integer amount) {
		User agent = userRepository.findUserByUserId(user_id);
		ShopItem item = shopRepository.findOne(Long.parseLong(shopId));
		ShopItemStock stock = new ShopItemStock();
		stock.setItem(item);
		stock.setCreationDate(new Date());
		stock.setAmount(amount);
		stock.setAgent(agent);
		return stockRepository.save(stock);
	}

	@Override
	public List<ShopItemStock> getAllStocks() {
		return stockRepository.findAll();
	}

}
