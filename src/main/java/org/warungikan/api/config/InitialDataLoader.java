package org.warungikan.api.config;

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
import org.warungikan.db.model.User;
import org.warungikan.db.repository.RoleRepository;
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

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event) {

		if (alreadySetup)
			return;
		
		createRoleIfNotFound("ROLE_ADMIN");
		createRoleIfNotFound("ROLE_USER");
		createRoleIfNotFound("ROLE_AGENT");

		createMasterUserIfNotFound();

		alreadySetup = true;
	}

	private void createMasterUserIfNotFound() {
		User r = userRepository.findUserByUserId("paniki");
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
			user.setEmail("greg.laksono@gmail.com");
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