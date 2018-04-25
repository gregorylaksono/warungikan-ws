package id.travel.api.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;

import id.travel.api.TravelLauncher;
import id.travel.api.service.IUserService;
import junit.framework.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= TravelLauncher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class ServiceTest {

	@Autowired
	IUserService userService;
	
	@Test
	public void addBalance(){
		User u = User.UserFactory("testname", "email1", "122344", "adres", "ccity", "2.993019", "4.2271113", "test");
		Role role = userService.getRoleByName("ROLE_USER");
		u.addRole(role);
		u.setBalance(0L);
		User customer =userService.register(u);
		Boolean topupSuccess = userService.addBalance(u.getEmail(), 100000L);
		Assert.assertNotNull(customer);
		Assert.assertTrue(topupSuccess);
	}
}
