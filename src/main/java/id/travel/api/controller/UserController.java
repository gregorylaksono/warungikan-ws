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

	@PostMapping("/register")
	public ResponseEntity register(@RequestBody User user){
		user.setName("test").setAddress("addr").setBalance(30000l).setUserid(user.getUserid()).
		setCity("depok").setEmail("greg.laksono@gmail.com").setEnable(true).setPassword(passwordEncoder.encode(user.getPassword())).
		setLatitude(30000d).setLongitude(12.90111D).setTelpNo("1232312323").setRoles(Arrays.asList(userService.getRoleByName("ROLE_USER"))).
		setCreationDate(new Date());
		userService.register(user);
		
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", user.getEmail()), HttpStatus.OK);
	}
	
	@GetMapping("/all")
	public ResponseEntity retrieveAllUser(){
		return new ResponseEntity(userService.getAllUsers(), HttpStatus.OK);
	}
	

}
