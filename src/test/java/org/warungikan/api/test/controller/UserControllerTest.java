package org.warungikan.api.test.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.warungikan.api.TravelLauncher;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.test.exception.UserSessionException;
import org.warungikan.api.test.exception.WarungIkanNetworkException;
import org.warungikan.api.test.manager.UserManagerImpl;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.ShopItemRepository;
import org.warungikan.db.repository.ShopItemStockRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.TransactionStateRepository;
import org.warungikan.db.repository.TransactionRepository;
import org.warungikan.db.repository.UserRepository;
@RunWith(SpringRunner.class)
@SpringBootTest(classes= TravelLauncher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class UserControllerTest {

	@Autowired
	IUserService userService;

	@Autowired
	UserRepository userRepository;
	
	@Test
	public void admin_test_get_single_user(){
		try {
			
			UserManagerImpl userManager = new UserManagerImpl();
			// Create user using admin role
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			String agentEmail = userManager.createUserAgent(jwt, "user12", "admin_test_get_single_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","3000");

			// Retrieve user by user id using admin role
			Assert.assertEquals("admin_test_get_single_user", agentEmail);
			User u = userManager.getSingleUserAsAdmin(jwt,"admin_test_get_single_user");
			Assert.assertNotNull(u);
			Assert.assertEquals("admin_test_get_single_user",u.getEmail());
			
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void admin_test_delete_single_user(){
		UserManagerImpl userManager = new UserManagerImpl();
		try {
			
			// Create user using admin role
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			String agentEmail = userManager.createUserAgent(jwt, "user1", "admin_test_delete_single_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","1000");

			// Retrieve user by user id using admin role
			Assert.assertEquals("admin_test_delete_single_user", agentEmail);
			Boolean isDeleted = userManager.deleteUser(jwt, "admin_test_delete_single_user");
			Assert.assertTrue(isDeleted);
			
			User u = userManager.getSingleUserAsAdmin(jwt, "admin_test_delete_single_user");
			Assert.assertNull(u);
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void admin_test_update_single_user(){
		UserManagerImpl userManager = new UserManagerImpl();
		try {
			
			// Create user using admin role
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			String agentEmail = userManager.createUserAgent(jwt, "user1", "admin_test_update_single_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","3000");

			// Retrieve user by user id using admin role
			Assert.assertEquals("admin_test_update_single_user", agentEmail);
			
			String updateName = "updatename";
			String updateEmail = "admin_test_update_single_user2";
			String updatetelno = "updatetelno";
			String updateAddress = "updateaddress";
			String updateCity = "updatecity";
			String updatelatitude = "1.928336361";
			String updatelongitude = "-81.008221";
			
			int rc = userManager.updateAgentAsAdmin(jwt, updateName,"admin_test_update_single_user", updateEmail, updatetelno, updateAddress, updateCity, updatelatitude, updatelongitude);
			Assert.assertEquals(rc, 200);
			      
			User u = userManager.getSingleUserAsAdmin(jwt, updateEmail);
			Assert.assertNotNull(u);
			Assert.assertEquals(updateName, u.getName());
			Assert.assertEquals(updateEmail, u.getEmail());
			Assert.assertEquals(updatetelno, u.getTelpNo());
			Assert.assertEquals(updateAddress, u.getAddress());
			Assert.assertEquals(updateCity, u.getCity());
			Assert.assertEquals(updatelatitude, String.valueOf(u.getLatitude()));
			Assert.assertEquals(updatelongitude, String.valueOf(u.getLongitude()));
			
			String jwt2 = userManager.login(updateEmail, "testpassword");
			Assert.assertNotNull(jwt2);
			
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void admin_test_get_all_user(){
		UserManagerImpl userManager = new UserManagerImpl();
		try {
			
			// Create user using admin role
			String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
			String emailAgent1 = userManager.createUserAgent(jwt, "user1", "admin_test_get_all_user1", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","2300");
			String emailAgent2 = userManager.createUserAgent(jwt, "user2", "admin_test_get_all_user2", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","2100");

			// Retrieve user by user id using admin role
			Assert.assertEquals("admin_test_get_all_user1", emailAgent1);
			Assert.assertEquals("admin_test_get_all_user2", emailAgent2);
			
			User u1 = userManager.getSingleUserAsAdmin(jwt, "admin_test_get_all_user1");
			User u2 = userManager.getSingleUserAsAdmin(jwt, "admin_test_get_all_user2");
			
			Assert.assertNotNull(u1);
			Assert.assertNotNull(u2);
			
			List<User> users = userManager.getAllUsers(jwt);
			
			Assert.assertTrue(users.contains(u1));
			Assert.assertTrue(users.contains(u2));
			
		} catch (UserSessionException | WarungIkanNetworkException e) {
			Assert.fail();
		}
	}
	
	

	@Test(expected = WarungIkanNetworkException.class)
	public void test_update_user_as_user() throws UserSessionException, WarungIkanNetworkException{
		
		UserManagerImpl userManager = new UserManagerImpl();
		String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
		String agentEmail = userManager.createUserAgent(jwt, "user1", "admin_test_get_all_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","1700");

		// Retrieve user by user id using admin role
		Assert.assertEquals("admin_test_get_all_user", agentEmail);

		// Attempt to get user as ADMIN
		jwt = userManager.login("email1", "testpassword");
		Integer u = userManager.updateAgentAsAdmin(jwt, "newName", "admin_test_get_all_user", "admin_test_get_all_user1", "telpnO", "ADdres", "CC", "6.0022", "-9.2211");
		
	}
	
	@Test(expected = UserSessionException.class)
	public void test_create_user_as_user() throws UserSessionException, WarungIkanNetworkException{
		
		UserManagerImpl userManager = new UserManagerImpl();
		String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
		String emailAgent = userManager.createUserAgent(jwt, "user1", "test_create_user_as_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","2000");

		// Retrieve user by user id using admin role
		Assert.assertEquals("test_create_user_as_user", emailAgent);

		// delete user as user role
		User u = userManager.getSingleUserAsAdmin(jwt, "test_create_user_as_user");
		jwt = userManager.login("test_create_user_as_user", "testpassword");
		
		String r = userManager.createUserAgent(jwt, "asdasd", u.getEmail(), "test_create_user_as_user2", "3asdasd", "asdasd3", "91.9921", "2.1123", "testestest","3000");
		
	}
	
	@Test(expected = UserSessionException.class)
	public void test_delete_user_as_user() throws UserSessionException, WarungIkanNetworkException{
		
		UserManagerImpl userManager = new UserManagerImpl();
		String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
		String agent = userManager.createUserAgent(jwt, "user1", "test_delete_user_as_user", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","3200");

		// Retrieve user by user id using admin role
		Assert.assertEquals("test_delete_user_as_user", agent);

		// delete user as user role
		jwt = userManager.login("test_delete_user_as_user", "testpassword");
		userManager.deleteUser(jwt,"test_delete_user_as_user");
	}

	@Test
	public void test_user_change_password() throws UserSessionException, WarungIkanNetworkException{
		UserManagerImpl userManager = new UserManagerImpl();
		String jwt = userManager.login("greg.laksono@gmail.com", "gregory1234");
		String gg = userManager.createUserAgent(jwt, "user1", "test_user_change_password5", "012394857", "address", "city", "1.33330", "-3.330022", "testpassword","2111");

		// Retrieve user by user id using admin role
		Assert.assertEquals("test_user_change_password5", gg);

		// login user as user role
		jwt = userManager.login("test_user_change_password5", "testpassword");
		User  u = userManager.getUserAsUser(jwt, "test_user_change_password5");
		Assert.assertNotNull(u);
		Boolean result = userManager.changePassword(jwt, u.getEmail(), "newPassword", u.getPassword());
		Assert.assertTrue(result);
		
		jwt = userManager.login("test_user_change_password5", "newPassword");
		u = userManager.getUserAsUser(jwt, "test_user_change_password5");
		
		Assert.assertNotNull(u);
	}

}
