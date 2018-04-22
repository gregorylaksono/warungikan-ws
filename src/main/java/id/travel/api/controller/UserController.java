package id.travel.api.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IUserService;

@RestController
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/user/{type}")
	public ResponseEntity registerCustomer(@RequestBody User user,@PathVariable("type") String type){
		
		Role roles = null;
		if(type.equalsIgnoreCase("admin")){
			roles = userService.getRoleByName("ROLE_ADMIN");
		}
		else if (type.equalsIgnoreCase("agent")){
			roles = userService.getRoleByName("ROLE_AGENT");
		}
		else {
			roles = userService.getRoleByName("ROLE_USER");
		}
		User u = registerUser(user, roles);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", u.getEmail()), HttpStatus.OK);
	}
	
	@GetMapping("/user/{user_id}")
	public ResponseEntity checkUserId(@PathVariable("user_id") String user_id){
		
		User u = userService.getUserById(user_id);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is exist", "SUCCESS", u.getEmail()), HttpStatus.OK);
	}
	
	@PutMapping("/user/{user_id}")
	public ResponseEntity updateUserById(@RequestBody User user){
		User u = userService.update(user);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", u.getEmail()), HttpStatus.OK);
	}


	@GetMapping("/user")
	public @ResponseBody List<User> getAllUser(){
		List<User> users = userService.getAllUsers();
		return users;
	}
	
	public User registerUser(User user, Role roles){
		if(user.getBalance() == null){
			user.setBalance(0L);
		}
		user.setEnable(true);
		user.addRole(roles);
		return userService.register(user);
		
	}
	
	@GetMapping("/all")
	public ResponseEntity retrieveAllUser(){
		return new ResponseEntity(userService.getAllUsers(), HttpStatus.OK);
	}
	

}
