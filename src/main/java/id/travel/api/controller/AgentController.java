package id.travel.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class AgentController {

	@Autowired
	private IUserService userService;
	
	@PostMapping("/register")
	@JsonView(View.Public.class)
	public ResponseEntity<BasicResponse> register(@RequestBody User user){
	
		User t = userService.register(user);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User successfuly registered", "SUCCES", t.getName()), HttpStatus.OK);
	}
	
	@GetMapping("/list/agent")
	@JsonView(View.Public.class)
	public ResponseEntity<List> getAgent(){
	
		List<User> t = userService.getAllAgents();
		return new ResponseEntity<List>(t, HttpStatus.OK);
	}
	
	@GetMapping("/login")
	@JsonView(View.Public.class)
	public ResponseEntity<BasicResponse> login(@RequestParam("userid") 	String userid, 
											   @RequestParam("password")String password){
	
		User t = userService.login(userid, password);
		return new ResponseEntity<BasicResponse>(new BasicResponse("User successfuly registered", "SUCCES", t.getName()), HttpStatus.OK);
	}
	
}
