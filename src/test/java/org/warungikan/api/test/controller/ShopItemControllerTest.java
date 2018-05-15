package org.warungikan.api.test.controller;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.warungikan.api.test.exception.UserSessionException;
import org.warungikan.api.test.exception.WarungIkanNetworkException;
import org.warungikan.api.test.manager.ShopItemManagerImpl;
import org.warungikan.api.test.manager.UserManagerImpl;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;

public class ShopItemControllerTest {

	@Test
	public void test(){
		ShopItemManagerImpl shopItemManager = new ShopItemManagerImpl();
		UserManagerImpl userManager = new UserManagerImpl();
		// Create user using admin role
		try {
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			ShopItem shopItem1 = shopItemManager.createShopItem(jwt, "item1", "item1 description", "http://item1.test", "30000","300");
			Assert.assertNotNull(shopItem1);
			
			ShopItem edit = shopItemManager.editShopItem(jwt, String.valueOf(shopItem1.getId()), "newName", "newDescription", "newUrl", "90000","300");
			Assert.assertEquals("newName", edit.getName());
			Assert.assertEquals("newDescription", edit.getDescription());
			Assert.assertEquals("newUrl", edit.getUrl());
			Assert.assertEquals(new Long("90000").longValue(), edit.getPrice().longValue());
			
			ShopItem shopItem2 = shopItemManager.createShopItem(jwt, "item12", "item12 description", "http://item12.test", "90000","300");
			List<ShopItem> items = shopItemManager.getShopItem(jwt);
			Assert.assertEquals(2, items.size());
			
			String agent = userManager.createUserAgent(jwt, "agentTest", "agent@email.com", "09922123", "Address", "city", "-99.928771", "-8.998261", "password","2500");
			Assert.assertNotNull(agent);
			Assert.assertEquals("agent@email.com", agent);
			
			ShopItemStock stock1 = shopItemManager.addStockByAgent(jwt, String.valueOf(shopItem1.getId()), agent, new Integer(10));
			ShopItemStock stock2 = shopItemManager.addStockByAgent(jwt, String.valueOf(shopItem2.getId()), agent, new Integer(20));
			
			Assert.assertNotNull(stock1);
			Assert.assertNotNull(stock2);
			
			Assert.assertEquals(new Integer(10), stock1.getAmount());
			Assert.assertEquals(new Integer(20), stock2.getAmount());
			
			String agentJwt = userManager.login(agent, "password");
			List<ShopItemStock> stocks = shopItemManager.getStockItem(agentJwt);
			Assert.assertNotNull(stocks);
			Assert.assertEquals(2, stocks.size());
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
