package id.travel.api.service;

import java.util.List;

import org.warungikan.db.model.User;

public interface IUserService {

	public User login(String email, String password);
	public User register(User user);
	public User generateUserId();
	public List<User> getAllAgents();
	public List<User> getAllUsers();
}
