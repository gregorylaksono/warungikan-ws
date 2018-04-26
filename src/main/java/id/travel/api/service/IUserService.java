package id.travel.api.service;

import java.io.Serializable;
import java.util.List;

import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;

public interface IUserService {

	public User login(String email, String password);
	public User registerAgentOrAdmin(User user, String price_per_km);
	public User generateUserId();
	public List<User> getAllAgents();
	public List<User> getAllUsers();
	public Role getRoleByName(String name);
	public User getUserById(String userId);
	public void setAvailableUser(String userId, Boolean enabled);
	public User update(String user_id, User user);
	public User delete(String user_id);
	public Boolean changePassword(String user_id, String password, String newPassword);
	public Boolean addBalance(String user_id, Long amount);
	public User registerUser(User user);
	public Long getPricePerKm(String agent);
}
