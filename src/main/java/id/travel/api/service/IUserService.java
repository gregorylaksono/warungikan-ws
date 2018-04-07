package id.travel.api.service;

import org.warungikan.db.model.User;

public interface IUserService {

	public User login(String email, String password);
	public User register(User user);
}
