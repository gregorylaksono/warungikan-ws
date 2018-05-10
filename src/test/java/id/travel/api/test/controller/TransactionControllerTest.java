package id.travel.api.test.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.warungikan.api.model.response.AgentStock;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.TransactionState;

import id.travel.api.test.exception.UserSessionException;
import id.travel.api.test.exception.WarungIkanNetworkException;
import id.travel.api.test.manager.ShopItemManagerImpl;
import id.travel.api.test.manager.TransactionManagerImpl;
import id.travel.api.test.manager.UserManagerImpl;

public class TransactionControllerTest {

	
	
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	     
	    mailSender.setUsername("greg.laksono@gmail.com");
	    mailSender.setPassword("qnubnnebvhothoxa");
	     
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.debug", "true");
	     
	    return mailSender;
	}
	
	@Test
	public void testEmail(){
		try{
			SimpleMailMessage mail = new SimpleMailMessage();
			mail.setTo("greg.laksono@gmail.com");
			mail.setText("test here");
			mail.setSubject("test subject");
			getJavaMailSender().send(mail);
			System.out.println("Success");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	@Test
	public void trxControllerTest() {
		
		UserManagerImpl userManager = new UserManagerImpl();
		TransactionManagerImpl trxManager = new TransactionManagerImpl();
		ShopItemManagerImpl shopItemManager = new ShopItemManagerImpl();

		String customerUserId =  "customer_"+UUID.randomUUID().toString().substring(0,6);
		String agent1UserId = "agent2_"+UUID.randomUUID().toString().substring(0,6);
		String agent2UserId = "agent1_"+UUID.randomUUID().toString().substring(0,6);
		try {
			
			String adminJwt = userManager.login("greg.laksono@gmail.com", "gregory1234");

			//Create customer
			userManager.createUserCustomer(adminJwt, "Greg",customerUserId, "222123", "address", "city", "-6.239884", "106.792410", "pwdpwd");

			//Add balance to customer
			Boolean customerBalanceAdded = trxManager.addBalanceUser(adminJwt, customerUserId, new Long("500000"));
			Assert.assertNotNull(customerBalanceAdded);
			Assert.assertTrue(customerBalanceAdded);

			//Check balance
			String customerJwt = userManager.login(customerUserId, "pwdpwd");
			Long customerBalance = trxManager.getBalanceCustomer(customerJwt, customerUserId);
			Assert.assertEquals(customerBalance, new Long("500000"));

			//Create agent 1
			agent1UserId = userManager.createUserAgent(adminJwt, "agent1greg", agent1UserId , "222334422", "agentaddress", "city", "-6.175361", "106.816445", "agentpassword","3500");
			String agent1Jwt = userManager.login(agent1UserId, "agentpassword");
			Assert.assertNotNull(agent1Jwt);

			//Create agent 2
			agent2UserId = userManager.createUserAgent(adminJwt, "agent2greg", agent2UserId , "222334422", "agentaddress", "city", "-6.390668", "106.825195", "agentpassword","2500");
			String agent2Jwt = userManager.login(agent2UserId, "agentpassword");
			Assert.assertNotNull(agent2Jwt);

			//Create shop item
			ShopItem shopItem1 = shopItemManager.createShopItem(adminJwt, "item1", "item1description", "http://url1.com", "50000");
			ShopItem shopItem2 = shopItemManager.createShopItem(adminJwt, "item2", "item2description", "http://url2.com", "75000");
			ShopItem shopItem3 = shopItemManager.createShopItem(adminJwt, "item3", "item3description", "http://url3.com", "43000");
			Assert.assertNotNull(shopItem1);
			Assert.assertNotNull(shopItem2);

			Assert.assertEquals("item1", shopItem1.getName());
			Assert.assertEquals("item1description", shopItem1.getDescription());
			Assert.assertEquals("http://url1.com", shopItem1.getUrl());
			Assert.assertEquals(new Long("50000").longValue(), shopItem1.getPrice().longValue());

			Assert.assertEquals("item2", shopItem2.getName());
			Assert.assertEquals("item2description", shopItem2.getDescription());
			Assert.assertEquals("http://url2.com", shopItem2.getUrl());
			Assert.assertEquals(new Long("75000").longValue(), shopItem2.getPrice().longValue());

			Assert.assertEquals("item3", shopItem3.getName());
			Assert.assertEquals("item3description", shopItem3.getDescription());
			Assert.assertEquals("http://url3.com", shopItem3.getUrl());
			Assert.assertEquals(new Long("43000").longValue(), shopItem3.getPrice().longValue());

			//Add stock to agent 1
			Integer startItemStock1 = 5;
			Integer startItemStock2 = 7;
			Integer startItemStock3 = 3;
			ShopItemStock stock1 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem1.getId()), agent1UserId, startItemStock1);
			ShopItemStock stock2 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem2.getId()), agent1UserId, startItemStock2);
			ShopItemStock stock3 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem3.getId()), agent1UserId, startItemStock3);
			Assert.assertNotNull(stock1);
			Assert.assertNotNull(stock2);
			Assert.assertNotNull(stock3);
			Assert.assertEquals(startItemStock1, stock1.getAmount());
			Assert.assertEquals(startItemStock2, stock2.getAmount());
			Assert.assertEquals(startItemStock3, stock3.getAmount());

			//Add stock to agent 2
			Integer startItemStock11 = 10;
			Integer startItemStock12 = 12;
			Integer startItemStock13 = 6;
			ShopItemStock stock11 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem1.getId()), agent2UserId, startItemStock11);
			ShopItemStock stock22 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem2.getId()), agent2UserId, startItemStock12);
			ShopItemStock stock33 = shopItemManager.addStockByAgent(adminJwt, String.valueOf(shopItem3.getId()), agent2UserId, startItemStock13);
			Assert.assertNotNull(stock1);
			Assert.assertNotNull(stock2);
			Assert.assertNotNull(stock3);
			Assert.assertEquals(startItemStock11, stock11.getAmount());
			Assert.assertEquals(startItemStock12, stock22.getAmount());
			Assert.assertEquals(startItemStock13, stock33.getAmount());

			TransactionDetail d1 = new TransactionDetail();
			d1.setAmount(5);
			d1.setItem(shopItem1);

			TransactionDetail d2 = new TransactionDetail();
			d2.setAmount(5);
			d2.setItem(shopItem2);

			TransactionDetail d3 = new TransactionDetail();
			d3.setAmount(2);
			d3.setItem(shopItem2);

			Set<TransactionDetail> details = new HashSet<>();
			details.add(d1);
			details.add(d2);

			String customerId = userManager.login(customerUserId, "pwdpwd");

			//Get available agents
			List<AgentStock> result = trxManager.getAgentBasedCustomerLocation(customerId, details);
			Assert.assertNotNull(result);
			Assert.assertTrue(result.size() == 2);

			//Calculate transport price
			AgentStock agent = findNearestAgent(result);
			Double km = (Double.parseDouble(agent.getTotal_distance()) / 1000);
			Long kmLong = Math.round(Math.ceil(km));
			Long transportPrice = trxManager.calculateTransportPrice(customerId, agent.getUser().getEmail(), kmLong);

			//Start transaction
			//Try negative transaction
			Transaction trx = trxManager.addTransaction(customerJwt, agent.getUser().getEmail(), transportPrice, transportPrice, details);
			Assert.assertNull(trx);

			//Check balance customer
			Long balanceCustomer = trxManager.getBalanceCustomer(customerId, customerUserId);
			Assert.assertEquals(new Long(500000), balanceCustomer);
			
			//Try positive transaction again
			details.clear();
			d1.setAmount(2);
			d2.setAmount(1);
			details.add(d1);
			details.add(d2);

			result = trxManager.getAgentBasedCustomerLocation(customerId, details);
			Assert.assertNotNull(result);
			Assert.assertTrue(result.size() == 2);

			agent = findNearestAgent(result);
			km = (Double.parseDouble(agent.getTotal_distance()) / 1000);
			kmLong = Math.round(Math.ceil(km));
			transportPrice = trxManager.calculateTransportPrice(customerId, agent.getUser().getEmail(), kmLong);
			trx = trxManager.addTransaction(customerJwt, agent.getUser().getEmail(), transportPrice, transportPrice, details);
			Assert.assertNotNull(trx);
			
			//Check balance customer
			balanceCustomer = trxManager.getBalanceCustomer(customerId, customerUserId);
			Assert.assertEquals(new Long(286500), balanceCustomer);
			
			//Check balance agent
			String agentJwt = userManager.login(agent1UserId, "agentpassword");
			Long balanceAgent = trxManager.getBalanceAgent(agentJwt, agent1UserId);
			Long actualPrice = trx.getTotalPrice() - transportPrice;
			Assert.assertEquals(balanceAgent, actualPrice);
			
			//Get transaction state
			List<TransactionState> states = trxManager.getTransactionState(customerId, String.valueOf(trx.getOid()));
			Assert.assertNotNull(states);
			Assert.assertTrue(states.size() == 1);
			
			List<Transaction> transactionAgents = trxManager.getTransactionAgent(agent1Jwt);
			List<Transaction> transactionCustomers = trxManager.getTransactionCustomer(customerJwt);
			
			Assert.assertNotNull(transactionAgents);
			Assert.assertNotNull(transactionCustomers);
			
			Assert.assertEquals(1, transactionCustomers.size());
			Assert.assertEquals(1, transactionAgents.size());
		} catch (UserSessionException | WarungIkanNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private AgentStock findNearestAgent(List<AgentStock> result) {
		long value = 0;
		AgentStock agent = null;

		for(AgentStock s : result){
			long distance = Long.parseLong(s.getTotal_distance());
			if(distance < value || value < 1){
				value = distance; 
				agent = s;
			}
		}
		return agent;
	}
}
