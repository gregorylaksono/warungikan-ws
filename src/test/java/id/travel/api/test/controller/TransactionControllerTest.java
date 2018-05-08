package id.travel.api.test.controller;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.warungikan.api.TravelLauncher;

import id.travel.api.test.exception.UserSessionException;
import id.travel.api.test.exception.WarungIkanNetworkException;
import id.travel.api.test.manager.TransactionManagerImpl;
import id.travel.api.test.manager.UserManagerImpl;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= TravelLauncher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class TransactionControllerTest {

	public void trxControllerTest() {
		UserManagerImpl userManager = new UserManagerImpl();
		TransactionManagerImpl trxManager = new TransactionManagerImpl();
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
			Integer agentCreated = userManager.createUserAgent(adminJwt, "agentgreg", agentUserId , "222334422", "agentaddress", "city", "-9.12344", "3.22123", "agentpassword");
			String agentJwt = userManager.login(agentUserId, "agentpassword");
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
}
