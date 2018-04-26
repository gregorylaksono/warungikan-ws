package id.travel.api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/user/{type}/{price_per_km}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity register(@RequestBody User user,@PathVariable( value = "type", required = true) String type,
									@PathVariable("price_per_km") String price_per_km){
		
		Role roles = null;
		if(type.equalsIgnoreCase("admin")){
			roles = userService.getRoleByName("ROLE_ADMIN");
		}
		else if (type.equalsIgnoreCase("agent")){
			roles = userService.getRoleByName("ROLE_AGENT");
		}
		
		User u = registerUser(user,price_per_km, roles);
		if(u != null){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", u.getEmail()), HttpStatus.OK);			
		}
		else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("User with id exists alread", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/user/{user_id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity checkUserId(@PathVariable(value = "user_id",required = true) String user_id){
		
		User u = userService.getUserById(user_id);
		return new ResponseEntity<User>(u, HttpStatus.OK);
	}
	
	@PutMapping("/user/{user_id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity updateUserById(@RequestBody User user, @PathVariable(value = "user_id", required = true) String user_id){
		User u = userService.update(user_id,user);
		if(u!=null){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User is updated", "SUCCESS", u.getEmail()), HttpStatus.OK);
		}else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("User with id exists alread", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/user/{user_id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity deleteUserById(@PathVariable(value = "user_id", required = true) String user_id){
		User u = userService.delete(user_id);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is deleted", "SUCCESS", u.getEmail()), HttpStatus.OK);
	}


	@GetMapping("/user")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public @ResponseBody List<User> getAllUser(){
		List<User> users = userService.getAllUsers();
		return users;
	}
	
	public User registerUser(User user, String price_per_km, Role roles){
		if(user.getBalance() == null){
			user.setBalance(0L);
		}
		user.setEnable(true);
		user.addRole(roles);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userService.registerAgentOrAdmin(user, price_per_km);
	}
	
	@GetMapping("/all")
	public ResponseEntity retrieveAllUser(){
		return new ResponseEntity(userService.getAllUsers(), HttpStatus.OK);
	}
}
