package id.travel.api.controller;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;
import org.warungikan.db.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import id.travel.api.model.BasicResponse;
import id.travel.api.model.UserLogin;
import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/register-customer")
	public ResponseEntity registerCustomer(@RequestBody User user){
		
		Role roles = userService.getRoleByName("ROLE_USER");
		User u = registerUser(user, roles);
		return new ResponseEntity<BasicResponse>(new BasicResponse("Customer is registered", "SUCCESS", u.getUserid()), HttpStatus.OK);
	}
	
	@PostMapping("/register-agent")
	public ResponseEntity registerAgent(@RequestBody User user){
		
		Role roles = userService.getRoleByName("ROLE_AGENT");
		User u = registerUser(user, roles);
		return new ResponseEntity<BasicResponse>(new BasicResponse("Agent is registered", "SUCCESS", u.getUserid()), HttpStatus.OK);
	}
	
	public User registerUser(User user, Role roles){
		user.setName("test").setAddress("addr").setBalance(0l).setUserid(user.getUserid()).
		setCity("depok").setEmail("greg.laksono@gmail.com").setEnable(true).setPassword(passwordEncoder.encode(user.getPassword())).
		setLatitude(30000d).setLongitude(12.90111D).setTelpNo("1232312323").
		
		setRoles(Arrays.asList(roles)).
		setCreationDate(new Date());
		return userService.register(user);
		
	}
	
	@GetMapping("/all")
	public ResponseEntity retrieveAllUser(){
		return new ResponseEntity(userService.getAllUsers(), HttpStatus.OK);
	}
	

}
