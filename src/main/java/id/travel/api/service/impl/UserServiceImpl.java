package id.travel.api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.service.IUserService;
import id.travel.api.utils.SecurityUtils;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public User login(String email, String password) {
		User u = userRepository.findByEmail(email);
		if(u.getPassword().equals(SecurityUtils.md5(password))){
			return u;
		}
		return null;
	}

	@Override
	public User register(User user) {
		String hashedPwd = SecurityUtils.md5(user.getPassword());
		user.setPassword(hashedPwd);
		userRepository.save(user);
		return user;
	}

}
