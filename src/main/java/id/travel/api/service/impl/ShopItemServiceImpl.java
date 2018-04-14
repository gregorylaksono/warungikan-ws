package id.travel.api.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.repository.ShopItemRepository;

import id.travel.api.service.IShopItemService;

@Service
public class ShopItemServiceImpl implements IShopItemService{

	private ShopItemRepository shopRepository;
	
	@Override
	public List<ShopItem> getAllShopItem() {

		return shopRepository.findAll();
	}

	@Override
	public void createShopItem(String name, String description, String url, String price) {
		ShopItem i = new ShopItem();
		i.setCreationDate(new Date());
		shopRepository.save(setValue(i, name, description, url, price));
	}

	@Override
	public void updateShopItem(String id, String name, String description, String url, String price) {
		ShopItem i = shopRepository.findOne(Long.parseLong(id));
		i.setLastModifiedDate(new Date());
		shopRepository.save(setValue(i, name, description, url, price));
	}
	
	private ShopItem setValue(ShopItem i, String name, String description, String url, String price){
		i.setDescription(description);
		i.setName(name);
		i.setPrice(Long.parseLong(price));
		i.setUrl(url);
		return i;
	}

}
