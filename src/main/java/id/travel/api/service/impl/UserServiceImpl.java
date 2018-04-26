package id.travel.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.service.IUserService;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private TopupWalletRepository topUpWalletRepository;
	
	@Override
	public User login(String email, String password) {
		
		return null;
	}

	@Override
	public User register(User user) {
		if(!isUserIdExist(user.getEmail())){
			user.setCreationDate(new Date());
			user.setEnable(true);
			user.setBalance(0l);
			return userRepository.save(user);			
		}else{
			return null;
		}
		
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
		return userRepository.findAllUsersEnabled();
	}

	@Override
	public Role getRoleByName(String name) {

		return roleRepository.findByName(name);
	}

	@Override
	public User getUserById(String userId) {
		return userRepository.findUserByUserId(userId);
	}

	@Override
	public void setAvailableUser(String userId, Boolean enabled) {
		User user = userRepository.findUserByUserId(userId);
		user.setEnable(enabled);
		user.setLastModifiedDate(new Date());
		userRepository.save(user);
	}

	@Override
	public User update(String user_id, User user) {
		if(!isUserIdExist(user.getEmail())){
			User u = userRepository.findUserByUserId(user_id);
			u.setLastModifiedDate(new Date());
			u.setAddress(user.getAddress());
			u.setAddressInfo(user.getAddressInfo());
			u.setCity(user.getCity());
			u.setEmail(user.getEmail());
			u.setEnable(user.getEnable());
			u.setLatitude(user.getLatitude());
			u.setLongitude(user.getLongitude());
			u.setName(user.getName());
			u.setPassword(user.getPassword());
			u.setRoles(getRoles(user.getRoles()));
			u.setTelpNo(user.getTelpNo());
			return userRepository.save(u);			
		}
		return null;
	}
	
	private List<Role> getRoles(Collection<Role> roles){
		List<Role> r = new ArrayList();
		for(Role s : roles){
			Role role = roleRepository.findByName(s.getName());
			r.add(role);
		}
		return r;
	}
	
	private boolean isUserIdExist(String user_id){
		User u = userRepository.findUserByUserId(user_id);
		return (u != null);
	}

	@Override
	public User delete(String email) {
		User user = userRepository.findUserByUserId(email);
		if(user != null) {
			user.setEnable(false);
			user.setLastModifiedDate(new Date());			
			return userRepository.save(user);
		}
		
		return null;
		
	}

	@Override
	public Boolean changePassword(String user_id, String password, String newPassword) {
		User u = userRepository.findUserByUserId(user_id);
		if(u.getPassword().equalsIgnoreCase(password)){
			u.setPassword(newPassword);
			userRepository.save(u);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Boolean addBalance(String user_id, Long amount) {
		User u = userRepository.findUserByUserId(user_id);
		if(u!=null){
			TopupWalletHistory t = new TopupWalletHistory();
			t.setAmount(amount);
			t.setUser(u);
			t.setCreationDate(new Date());
			u.setBalance(u.getBalance() + amount);
			topUpWalletRepository.save(t);
			userRepository.save(u);
			return true;
		}
		return true;
	}


}
