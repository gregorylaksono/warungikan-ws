package id.travel.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.User;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/balance")
public class BalanceController {
	@Autowired
	private IUserService userService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	
	@PostMapping("/user/{user_id}/{balance}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity addBalanceUser(@PathVariable("user_id") String user_id,
											@PathVariable("balance") String balance){
		
		Long bal = null;
		try{
			bal = Long.parseLong(balance);
			Boolean result = userService.addBalance(user_id, bal);
			if(result){
				return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is added", "SUCCESS", ""), HttpStatus.OK);
			}else{
				return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is failed to add", "FAILEd", ""), HttpStatus.OK);
			}
		}catch(Exception e){
			return new ResponseEntity<BasicResponse>(new BasicResponse("Balance is failed to add", "FAILEd", ""), HttpStatus.OK);
		}
		
	}
}
