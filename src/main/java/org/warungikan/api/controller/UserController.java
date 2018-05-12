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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.api.model.BasicResponse;
import org.warungikan.api.model.ChangePassword;
import org.warungikan.api.model.request.VLatLng;
import org.warungikan.api.service.IUserService;
import org.warungikan.api.utils.Constant;
import org.warungikan.api.utils.SecurityUtils;
import org.warungikan.db.model.AgentData;
import org.warungikan.db.model.Role;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/user/register")
	public ResponseEntity register(@RequestBody User user){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User u = userService.registerUser(user);
		if(u != null){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", u.getEmail()), HttpStatus.ACCEPTED);			
		}
		else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("User with id exists already", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/user/verify")
	public ResponseEntity verify(@RequestParam(value = "verification_id", required = true) String verification_id){
		if(userService.enableUser(verification_id)){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User is successfull verified", "SUCCESS", ""), HttpStatus.ACCEPTED);			
		}
		else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("Can not verify user", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
	public ResponseEntity getMyData(HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			User u = userService.getUserById(user_id);
			return new ResponseEntity<User>(u, HttpStatus.OK);
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Can not retrieve user","FAILED",""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/user")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity updateUserById( @RequestBody User user, HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			User u = userService.update(user_id,user);
			if(u != null){
				return new ResponseEntity<BasicResponse>(new BasicResponse("User is updated", "SUCCESS", u.getEmail()), HttpStatus.ACCEPTED);
			}else{
				return new ResponseEntity<BasicResponse>(new BasicResponse("Can not update user","FAILED",""), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Can not update user","FAILED",""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/user/enable")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity enableUser(HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			if(userService.enableUser(user_id)){
				return new ResponseEntity<BasicResponse>(new BasicResponse("User is enabled", "SUCCESS", ""), HttpStatus.ACCEPTED);
			}else{
				return new ResponseEntity<BasicResponse>(new BasicResponse("User can not be enabled", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("User can not be enabled", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/user/change_password/{user_id}")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity changePassword(@PathVariable(value = "user_id", required = true) String user_id, @RequestBody ChangePassword chPassword){
		Boolean result = userService.changePassword(user_id, chPassword.getPassword(),
				passwordEncoder.encode(chPassword.getNewPassword()));
		if(result){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Password is updated", "SUCCESS", String.valueOf(result)), HttpStatus.ACCEPTED);
		}else{
			return new ResponseEntity<BasicResponse>(new BasicResponse("Old password is wrong", "FAILED", String.valueOf(result)), HttpStatus.ACCEPTED);
		}
	}
	
	@GetMapping("/user/data")
	@PreAuthorize("hasRole('ROLE_AGENT')")
	public ResponseEntity getAgentData(HttpServletRequest request){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			AgentData agent = userService.getAgentData(user_id);
			return new ResponseEntity<AgentData>(agent, HttpStatus.ACCEPTED);
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Can not retrieve agent data", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/user/coordinate")
	@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_AGENT')")
	public ResponseEntity changeLocation(HttpServletRequest request, @RequestBody VLatLng coordinate){
		try{
			String token = request.getHeader(Constant.HEADER_STRING);
			String user_id = SecurityUtils.getUsernameByToken(token);
			Boolean result = userService.changeCoordinate(user_id, Double.parseDouble(coordinate.getLat()), Double.parseDouble(coordinate.getLng()));
			if(result){
				return new ResponseEntity<BasicResponse>(new BasicResponse("Coordinate is updated", "SUCCESS", String.valueOf(result)), HttpStatus.ACCEPTED);
			}else{
				return new ResponseEntity<BasicResponse>(new BasicResponse("Coordinate can not be updated", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Coordinate can not be updated", "FAILED", ""), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
