package id.travel.api.test;

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
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.ShopItemStock;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.Transaction;
import org.warungikan.db.model.TransactionDetail;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.ShopItemRepository;
import org.warungikan.db.repository.ShopItemStockRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.TransactionDetailRepository;
import org.warungikan.db.repository.TransactionRepository;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.TravelLauncher;
import id.travel.api.service.IUserService;
@RunWith(SpringRunner.class)
@SpringBootTest(classes= TravelLauncher.class)
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Transactional
public class DBTest {

	
	
	@Autowired
	IUserService userService;


	@Test
	public void test(){
		List<User> all = userService.getAllUsers();
		all.size();
	}
	
}
