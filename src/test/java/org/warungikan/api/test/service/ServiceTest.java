package org.warungikan.api.test.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.warungikan.api.Launcher;
import org.warungikan.api.model.response.AgentStock;
import org.warungikan.api.service.IShopItemService;
import org.warungikan.api.service.ITransactionService;
import org.warungikan.api.service.IUserService;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;
import org.warungikan.db.model.User;

import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Launcher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class ServiceTest {

	@Autowired
	IUserService userService;
	
	@Autowired
	IShopItemService shopItemService;
	
	@Autowired
	ITransactionService transactionService;
	
	@Test
	public void getAgentsByUserLocation(){
		User customer = User.UserFactory("agent", "userEmail", "telpNo", "address", "city", "-6.262776", "106.829766", "passw");
		User agent = User.UserFactory("customer", "agent1@mail.com", "3333", "213342", "addd", "-6.229885", "106.833500", "pwd");
		customer = userService.registerUser(customer);
		agent = userService.registerUser(agent, Arrays.asList(new String[] {"ROLE_USER","ROLE_AGENT"}), "3000");
		
		User agent2 = User.UserFactory("agent2", "agent2@mail.com", "33322", "5555", "city", "-6.193074", "106.889019", "pass");
		agent2 = userService.registerUser(agent2, Arrays.asList(new String[] {"ROLE_USER","ROLE_AGENT"}), "3000");
		
		User agent3 = User.UserFactory("agent3", "agent3@mail.com", "33322", "5555", "city", "-6.200792", "106.815811", "pass");
		agent3 = userService.registerUser(agent3, Arrays.asList(new String[] {"ROLE_USER","ROLE_AGENT"}), "3000");
		
		ShopItem item1 = shopItemService.createShopItem("item1", "item1desc", "http://sdsdas", "50000","300");
		ShopItem item2 = shopItemService.createShopItem("item2", "item2desc", "http://sdsdas", "60000","300");
		ShopItem item3 = shopItemService.createShopItem("item2", "item2desc", "http://sdsdas", "40000","300");
		
		ShopItemStock stock1 = shopItemService.addStock(String.valueOf(item1.getId()), agent.getEmail(), new Integer("5"));
		ShopItemStock stock2 = shopItemService.addStock(String.valueOf(item2.getId()), agent.getEmail(), new Integer("10"));
		ShopItemStock stock3 = shopItemService.addStock(String.valueOf(item3.getId()), agent.getEmail(), new Integer("7"));
		
		ShopItemStock stock1a = shopItemService.addStock(String.valueOf(item1.getId()), agent2.getEmail(), new Integer("5"));
		ShopItemStock stock1b = shopItemService.addStock(String.valueOf(item2.getId()), agent2.getEmail(), new Integer("5"));
		
		ShopItemStock stock2b = shopItemService.addStock(String.valueOf(item2.getId()), agent3.getEmail(), new Integer("10"));
		TransactionDetail d1 = new TransactionDetail();
		d1.setAmount(5);
		d1.setItem(item1);
		
		TransactionDetail d2 = new TransactionDetail();
		d2.setAmount(5);
		d2.setItem(item2);
		
		TransactionDetail d3 = new TransactionDetail();
		d3.setAmount(2);
		d3.setItem(item3);
		
		Set<TransactionDetail> details = new HashSet<>();
		details.add(d1);
		details.add(d2);
//		details.add(d3);
		
		List<AgentStock> stocks = transactionService.getAgentBasedCustomerLocation(details, customer.getEmail());
		Assert.assertNotNull(stocks);
	}
	
	public void testTransaction(){
		//Create customer 
		User u = User.UserFactory("customer", "customer1", "122344", "adres", "ccity", "2.993019", "4.2271113", "test");
	
		User customer = userService.registerUser(u, Arrays.asList(new String[]{"ROLE_USER"}),null);
		Assert.assertNotNull(customer);
		Assert.assertTrue(customer.getEnable());
		
		//Add balance to customer Rp. 500.000
		Boolean topupSuccess = userService.addBalance(u.getEmail(), new Long(500000), new Date(), "GR-123333");
		Assert.assertNotNull(customer);
		Assert.assertTrue(topupSuccess);
		customer = userService.getUserById("customer1");
		Assert.assertEquals((Long)500000L, u.getBalance());
		Assert.assertNotNull(customer);
		Long currentBalanceCustomer = transactionService.calculateBalanceCustomer("customer1");
		Assert.assertEquals((Long)500000L, currentBalanceCustomer);
		
		//Create agent
		User agent = User.UserFactory("agent", "agent1", "122344", "adres", "ccity", "2.993019", "8.2271113", "test");
		agent = userService.registerUser(agent, Arrays.asList(new String[]{"ROLE_AGENT"}), "4500");
		Assert.assertNotNull(agent);
		Assert.assertTrue(agent.getEnable());
		
		//Create shop item
		ShopItem item1 = shopItemService.createShopItem("item1", "item1desc", "https://", "30000","300");
		ShopItem item2 = shopItemService.createShopItem("item2", "item2desc", "https://", "45000","300");
		Assert.assertNotNull(item1);
		Assert.assertNotNull(item2);
		
		Assert.assertEquals(item1.getName(), "item1");
		Assert.assertEquals(item1.getDescription(), "item1desc");
		Assert.assertEquals(item1.getUrl(), "https://");
		Assert.assertEquals(item1.getPrice(), new Long("30000"));
		
		Assert.assertEquals(item2.getName(), "item2");
		Assert.assertEquals(item2.getDescription(), "item2desc");
		Assert.assertEquals(item2.getUrl(), "https://");
		Assert.assertEquals(item2.getPrice(), new Long("45000"));
		
		//Create stock for agent
		Integer startItemStock1 = 5;
		Integer startItemStock2 = 7;
		ShopItemStock stock1 = shopItemService.addStock(String.valueOf(item1.getId()), agent.getEmail(), startItemStock1);
		ShopItemStock stock2 = shopItemService.addStock(String.valueOf(item2.getId()), agent.getEmail(), startItemStock2);
		Assert.assertNotNull(stock1);
		Assert.assertNotNull(stock2);
		
		Assert.assertEquals(stock1.getAgent(), agent);
		Assert.assertEquals(stock1.getAmount(), startItemStock1);
		Assert.assertNotNull(stock1.getCreationDate());
		Assert.assertNotNull(stock1.getOid());
		
		Assert.assertEquals(stock2.getAgent(), agent);
		Assert.assertEquals(stock2.getAmount(), startItemStock2);
		Assert.assertNotNull(stock2.getCreationDate());
		Assert.assertNotNull(stock2.getOid());
		
		//Start transaction
		//Create detail first
		Long transportPricePerKM = new Long(5000);
		Set<TransactionDetail> details = createDummyTrxDetails(new ShopItem[]{item1, item2});
		
		//Calculate transport price. With distance 15km
		Long totalTransportPrice = transactionService.calculateTransportPrice("15", agent.getEmail());
		Assert.assertEquals(67500L, totalTransportPrice.longValue());
		
		//Start transaction
		Transaction t = transactionService.addTransaction(customer.getEmail(), agent.getEmail(), details, totalTransportPrice,0L);
//		Assert.assertEquals(details.size(), t.getTransactionDetails().size());
		Assert.assertEquals(customer.getEmail(), t.getCustomer().getEmail());
		Assert.assertEquals(agent.getEmail(), t.getAgent().getEmail());
		
		//Check balance
		Long balanceAgent = transactionService.calculateBalanceAgent(agent.getEmail());
		Long balanceCustomer = transactionService.calculateBalanceCustomer(customer.getEmail());
		
		Assert.assertEquals(282500L, balanceCustomer.longValue());
		Assert.assertEquals(150000L, balanceAgent.longValue());
		
		//Check state
		List<TransactionState> states = transactionService.getTransactionState(String.valueOf(t.getOid()));
		Assert.assertEquals(1, states.size());
				
		TransactionState state = transactionService.markTransactionAsProcessing(t.getOid());
		states = transactionService.getTransactionState(String.valueOf(t.getOid()));
		Assert.assertEquals(2, states.size());
		
		state = transactionService.markTransactionAsPaid(t.getOid());
		states = transactionService.getTransactionState(String.valueOf(t.getOid()));
		Assert.assertEquals(3, states.size());
		
		state = transactionService.markTransactionAsDelivering(t.getOid());
		states = transactionService.getTransactionState(String.valueOf(t.getOid()));
		Assert.assertEquals(4, states.size());
	}
	
	private Set<TransactionDetail> createDummyTrxDetails(ShopItem[] items){
		Set<TransactionDetail> details = new HashSet();
		for(ShopItem i: items){
			TransactionDetail d1 = new TransactionDetail();
			d1.setItem(i);
			d1.setAmount(2);
			details.add(d1);
		}
		return details;
	}
}
