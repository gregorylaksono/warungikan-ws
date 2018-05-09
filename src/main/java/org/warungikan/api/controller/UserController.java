package org.warungikan.api.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.api.model.BasicResponse;
import org.warungikan.api.model.ChangePassword;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.utils.Constant;
import org.warungikan.api.utils.SecurityUtils;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/user")
	public ResponseEntity register(@RequestBody User user){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User u = userService.registerUser(user);
		if(u != null){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", u.getEmail()), HttpStatus.OK);			
		}
		else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("User with id exists alread", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/user/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity getMyData(@PathVariable(value = "user_id", required=true) String user_id, HttpServletRequest request){
	    String token = request.getHeader(Constant.HEADER_STRING);
	    String username = SecurityUtils.getUsernameByToken(token);
	    
		User u = userService.getUserById(user_id);
		if(username.equals(u.getEmail())){
			return new ResponseEntity<User>(u, HttpStatus.OK);	
		}
		else{
			return new ResponseEntity<User>(u, HttpStatus.NOT_FOUND);	
		}
	}

	@PutMapping("/user/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity updateUserById(@PathVariable(value = "user_id", required = true) String user_id, @RequestBody User user){
		User u = userService.update(user_id,user);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is updated", "SUCCESS", u.getEmail()), HttpStatus.OK);
	}
	
	@PutMapping("/user/change_password/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity changePassword(@PathVariable(value = "user_id", required = true) String user_id, @RequestBody ChangePassword chPassword){
		Boolean result = userService.changePassword(user_id, chPassword.getPassword(),
				passwordEncoder.encode(chPassword.getNewPassword()));
		if(result){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Password is updated", "SUCCESS", String.valueOf(result)), HttpStatus.OK);
		}else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("Old password is wrong", "FAILED", String.valueOf(result)), HttpStatus.OK);
		}
	}
	
	
}
