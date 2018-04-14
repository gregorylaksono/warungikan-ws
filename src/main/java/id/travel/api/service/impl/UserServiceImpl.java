package id.travel.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Override
	public User login(String email, String password) {
		
		return null;
	}

	@Override
	public User register(User user) {
		
		return userRepository.save(user);
	}

	@Override
	public User generateUserId() {
		
		return null;
	}

	@Override
	public List<User> getAllAgents() {
		return userRepository.findAllAgent();
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public Role getRoleByName(String name) {

		return roleRepository.findByName(name);
	}

	@Override
	public User getUserById(String userId) {
		return userRepository.findUserByUserId(userId);
	}

}
