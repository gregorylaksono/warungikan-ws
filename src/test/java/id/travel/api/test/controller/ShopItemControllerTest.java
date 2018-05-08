package id.travel.api.test.controller;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.warungikan.api.controller.ShopItemController;
import org.warungikan.db.model.ShopItem;

import id.travel.api.test.exception.UserSessionException;
import id.travel.api.test.exception.WarungIkanNetworkException;
import id.travel.api.test.manager.ShopItemManagerImpl;
import id.travel.api.test.manager.UserManagerImpl;

public class ShopItemControllerTest {

	@Test
	public void test(){
		ShopItemManagerImpl shopItemManager = new ShopItemManagerImpl();
		UserManagerImpl userManager = new UserManagerImpl();
		// Create user using admin role
		try {
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			ShopItem shopItem1 = shopItemManager.createShopItem(jwt, "item1", "item1 description", "http://item1.test", "30000");
			Assert.assertNotNull(shopItem1);
			
			ShopItem edit = shopItemManager.editShopItem(jwt, String.valueOf(shopItem1.getId()), "newName", "newDescription", "newUrl", "90000");
			Assert.assertEquals("newName", edit.getName());
			Assert.assertEquals("newDescription", edit.getDescription());
			Assert.assertEquals("newUrl", edit.getUrl());
			Assert.assertEquals(new Long("90000").longValue(), edit.getPrice().longValue());
			ShopItem shopItem2 = shopItemManager.createShopItem(jwt, "item12", "item12 description", "http://item12.test", "90000");
			List<ShopItem> items = shopItemManager.getShopItem(jwt);
			
			
			Assert.assertEquals(2, items.size());
		} catch (UserSessionException | WarungIkanNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
