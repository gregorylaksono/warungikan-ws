package id.travel.api.test.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.warungikan.api.TravelLauncher;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;

import id.travel.api.test.exception.UserSessionException;
import id.travel.api.test.exception.WarungIkanNetworkException;
import id.travel.api.test.manager.ShopItemManagerImpl;
import id.travel.api.test.manager.TransactionManagerImpl;
import id.travel.api.test.manager.UserManagerImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= TravelLauncher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class TransactionControllerTest {

	@Test
	public void trxControllerTest() {
		UserManagerImpl userManager = new UserManagerImpl();
		TransactionManagerImpl trxManager = new TransactionManagerImpl();
		ShopItemManagerImpl shopItemManager = new ShopItemManagerImpl();
		
		String customerUserId =  "gregtest@email.com";
		String agentUserId = "agentgreg@email.com";
		try {
			String adminJwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			
			//Create customer
			userManager.createUserCustomer(adminJwt, "Greg",customerUserId, "222123", "address", "city", "2.33223", "-2.54432", "pwdpwd");
			
			//Add balance to customer
			Boolean customerBalanceAdded = trxManager.addBalanceUser(adminJwt, customerUserId, new Long("500000"));
			Assert.assertNotNull(customerBalanceAdded);
			Assert.assertTrue(customerBalanceAdded);
			
			//Check balance
			String customerJwt = userManager.login(customerUserId, "pwdpwd");
			Long customerBalance = trxManager.getBalanceCustomer(customerJwt, customerUserId);
			Assert.assertEquals(customerBalance, new Long("500000"));
			
			//Create agent
			agentUserId = userManager.createUserAgent(adminJwt, "agentgreg", agentUserId , "222334422", "agentaddress", "city", "-9.12344", "3.22123", "agentpassword","2500");
			String agentJwt = userManager.login(agentUserId, "agentpassword");
			Assert.assertNotNull(agentJwt);
			
			//Create shop item
			ShopItem shopItem1 = shopItemManager.createShopItem(adminJwt, "item1", "itemdescription", "http://url.com", "50000");
			ShopItem shopItem2 = shopItemManager.createShopItem(adminJwt, "item2", "itemdescription", "http://url.com", "50000");
			Assert.assertNotNull(shopItem1);
			Assert.assertNotNull(shopItem2);
			
			Assert.assertEquals("item1", shopItem1.getName());
			Assert.assertEquals("itemdescription", shopItem1.getDescription());
			Assert.assertEquals("http://url.com", shopItem1.getUrl());
			Assert.assertEquals(new Long("50000").longValue(), shopItem1.getPrice().longValue());
			
			Assert.assertEquals("item2", shopItem2.getName());
			Assert.assertEquals("itemdescription", shopItem2.getDescription());
			Assert.assertEquals("http://url.com", shopItem2.getUrl());
			Assert.assertEquals(new Long("50000").longValue(), shopItem2.getPrice().longValue());
			
			//Add stock to agent
			Integer startItemStock1 = 5;
			Integer startItemStock2 = 7;
			ShopItemStock stock1 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem1.getId()), agentUserId, startItemStock1);
			ShopItemStock stock2 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem2.getId()), agentUserId, startItemStock2);
			Assert.assertNotNull(stock1);
			Assert.assertNotNull(stock2);
			Assert.assertEquals(startItemStock1, stock1.getAmount());
			Assert.assertEquals(startItemStock2, stock2.getAmount());
			
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
