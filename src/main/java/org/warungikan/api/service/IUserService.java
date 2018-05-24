package org.warungikan.api.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.warungikan.db.model.AgentData;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.TopupWalletHistory;
import org.warungikan.db.model.User;

public interface IUserService {

	public User login(String email, String password);
	public User registerUser(User user,List<String> roles, String price_per_km);
	public User generateUserId();
	public List<User> getAllAgents();
	public List<User> getAllUsers();
	public Role getRoleByName(String name);
	public User getUserById(String userId);
	public void setAvailableUser(String userId, Boolean enabled);
	public User update(String user_id, User user);
	public User delete(String user_id);
	public Boolean changePassword(String user_id, String password, String newPassword);
	public Boolean addBalance(String user_id, Long amount, Date topupDate, String refNo);
	public User registerUser(User user);
	public Long getPricePerKm(String agent);
	public List<Role> getRoles(String userId);
	public Boolean enableUser(String verification_id);
	public AgentData getAgentData(String user_id);
	public Boolean changeCoordinate(String user_id, Double lat, Double lng);
}
