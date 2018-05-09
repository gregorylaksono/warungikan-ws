package org.warungikan.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.utils.Constant;
import org.warungikan.db.model.AgentData;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.AgentDataRepository;
import org.warungikan.db.repository.RoleRepository;
import org.warungikan.db.repository.TopupWalletRepository;
import org.warungikan.db.repository.UserRepository;

import com.google.gson.Gson;

@Service
public class UserServiceImpl implements IUserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private TopupWalletRepository topUpWalletRepository;
	
	@Autowired
	private AgentDataRepository agentDataRepository;
	
	@Override
	public User login(String email, String password) {
		
		return null;
	}

	@Override
	public User registerUser(User user,List<String> roles, String pricePerKm) {
		if(!isUserIdExist(user.getEmail())){
			List<Role> rolesDb = roleRepository.findRolesByArrayName(roles);
			user.setCreationDate(new Date());
			user.setEnable(true);
			user.setBalance(0l);
			user.addAllRole(rolesDb);
			user = userRepository.save(user);
			if(pricePerKm != null){
				Long.parseLong(pricePerKm);
				AgentData d = new AgentData();
				Map<String, String> data = new HashMap<>();
				data.put(Constant.AGENT_DATA_KEY_PRICE_PER_KM, pricePerKm);
				String json = new Gson().toJson(data);
				d.setData(json);
				d.setAgent(user);
				agentDataRepository.save(d);
			}
			return user;
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
			List<Role> roles = roleRepository.findRoleByUser(user_id);
			if(roles.stream().filter( r -> r.getName().equals("ROLE_USER")).collect(Collectors.toList()).size() > 0) {
				TopupWalletHistory t = new TopupWalletHistory();
				t.setAmount(amount);
				t.setUser(u);
				t.setCreationDate(new Date());
				u.setBalance(u.getBalance() + amount);
				topUpWalletRepository.save(t);
				userRepository.save(u);
				return true;
			}
		}
		return false;
	}

	@Override
	public User registerUser(User user) {
		Role r = getRoleByName("ROLE_USER");
		
		user.setBalance(0l).addRole(r).
		setEnable(true).setCreationDate(new Date());
		
		return userRepository.save(user);
	}

	@Override
	public Long getPricePerKm(String agent_id) {
		User u = userRepository.findUserByUserId(agent_id);
		AgentData data =agentDataRepository.findDataByUser(u);
		if(data != null){
			Map m = new Gson().fromJson(data.getData(), Map.class);
			String sAgentRate = String.valueOf( m.get(Constant.AGENT_DATA_KEY_PRICE_PER_KM));
			return Long.parseLong(sAgentRate);
		}else{
			return null;
		}
	}

	@Override
	public List<Role> getRoles(String userId) {
		List<Role>roles = roleRepository.findRoleByUser(userId);
		return roles;
	}


}
