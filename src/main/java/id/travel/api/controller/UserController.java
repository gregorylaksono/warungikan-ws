package id.travel.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.User;
import org.warungikan.db.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private IUserService userService;
	
	@GetMapping("/login")
	@JsonView(View.Public.class)
	public ResponseEntity<User> login(@RequestParam("email") String email,
									  @RequestParam("password") String password){
		User u = userService.login(email, password);
		
		return new ResponseEntity<User>(u, HttpStatus.OK);
	}
	
	@PostMapping("/register")
	public ResponseEntity<BasicResponse> register(@RequestBody User user){
		
		userService.register(user);
		
		return new ResponseEntity<BasicResponse>(new BasicResponse("User is registered", "SUCCESS", user.getEmail()), HttpStatus.OK);
	}
	

}
