package org.warungikan.api.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.ShopItem;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.ShopItemRepository;
import org.warungikan.db.repository.UserRepository;

@Component
public class InitialDataLoader implements
ApplicationListener<ContextRefreshedEvent> {

	boolean alreadySetup = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private ShopItemRepository shopItemRepository;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (alreadySetup)
			return;
		createRoleIfNotFound("ROLE_ADMIN");
		createRoleIfNotFound("ROLE_USER");
		createRoleIfNotFound("ROLE_AGENT");
		createMasterUserIfNotFound();
		createShopItem();
		alreadySetup = true;
	}

	private void createShopItem() {
		List<ShopItem> all = shopItemRepository.findAll();
		if(all.size() < 17){
			shopItemRepository.deleteAll();
			List<ShopItem> fishes = new ArrayList();
			fishes.add(ShopItem.createNew("Gindara", "http://warungikan.com/images/produk/WIN001.jpg", 30000L, "200gr", "Gindara", true));
			fishes.add(ShopItem.createNew("Cobia", "http://warungikan.com/images/produk/WIN002.jpg",30000L , "200gr", "Cobia", true));
			fishes.add(ShopItem.createNew("Sunu Ekor Bulan (Utuh)", "http://warungikan.com/images/produk/WIN004.jpg",60000L , "300-500gr", "Sunu Ekor Bulan (Utuh)", true));
			fishes.add(ShopItem.createNew("Kakap Merah Chinaman", "http://warungikan.com/images/produk/WIN003.jpg", 30000L, "150gr", "Kakap Merah Chinaman", true));
			fishes.add(ShopItem.createNew("Sunu Lodi (Utuh)", "http://warungikan.com/images/produk/WIN005.jpg",60000L , "300-500 Gr", "Sunu Lodi (Utuh)", true));
			fishes.add(ShopItem.createNew("Tuna Sirip Kuning", "http://warungikan.com/images/produk/WIN006.jpg", 25000L, "100 Gr", "Tuna Sirip Kuning", true));
			fishes.add(ShopItem.createNew("Marlin", "http://warungikan.com/images/produk/WIN007.jpg",30000L , "150 Gr", "Marlin", true));
			fishes.add(ShopItem.createNew("Salmon", "http://warungikan.com/images/produk/WIN008.jpg", 35000L, "100 Gr", "Salmon", true));
			fishes.add(ShopItem.createNew("Barramundi", "http://warungikan.com/images/produk/WIN009.jpg",45000L , "200 Gr", "Barramundi", true));
			fishes.add(ShopItem.createNew("Mahi Mahi", "http://warungikan.com/images/produk/WIN010.jpg",30000L , "200 Gr", "Mahi Mahi", true));
			fishes.add(ShopItem.createNew("Lencam", "http://warungikan.com/images/produk/WIN011.jpg",30000L , "150 Gr", "Lencam", true));
			fishes.add(ShopItem.createNew("Kakatua", "http://warungikan.com/images/produk/WIN012.jpg", 40000L, "150 Gr", "Kakatua", true));
			fishes.add(ShopItem.createNew("Biji Nangka (Utuh)", "http://warungikan.com/images/produk/WIN013.jpg", 30000L, "200-300 Gr", "Biji Nangka (Utuh)", true));
			fishes.add(ShopItem.createNew("Roa (Utuh)", "http://warungikan.com/images/produk/WIN015.jpg", 25000L, "200 Gr", "Roa (Utuh)", true));
			fishes.add(ShopItem.createNew("Kaci Kaci", "http://warungikan.com/images/produk/WIN028.jpg", 30000L, "150 Gr", "Kaci Kaci", true));
			fishes.add(ShopItem.createNew("Katarap", "http://warungikan.com/images/produk/WIN029.jpg",40000L , "150 Gr", "Katarap", true));
			fishes.add(ShopItem.createNew("Biji Nangka (Utuh)", "http://warungikan.com/images/produk/WIN031.jpg", 35000L, "300 - 500 Gr", "Biji Nangka (Utuh)", true));
			shopItemRepository.save(fishes);
		}
		
	}

	private void createMasterUserIfNotFound() {
		User r = userRepository.findUserByUserId("greg@laksono.com");
		if(r == null){
			Role adminRole = roleRepository.findByName("ROLE_ADMIN");
			User user = new User();
			user.setName("test");
			user.setAddress("no-value");
			user.setBalance(0L);
			user.setCity("no-value");
			user.setCreationDate(new Date());
			user.setEmail("no-value");
			user.setEnable(true);
			user.setLatitude(0d);
			user.setLongitude(0d);
			user.setTelpNo("no-value");
			user.setPassword(passwordEncoder.encode("gregory1234"));
			user.setEmail("greg@laksono.com");
			user.setRoles(Arrays.asList(adminRole));
			user.setEnable(true);
			userRepository.save(user);					
		}
	}


	@Transactional
	private Role createRoleIfNotFound(
			String name) {

		Role role = roleRepository.findByName(name);
		if (role == null) {
			role = new Role(name);
			roleRepository.save(role);
		}
		return role;
	}
}