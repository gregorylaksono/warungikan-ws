package id.travel.api.controller;

import java.security.Principal;
import java.util.ArrayList;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import id.travel.api.service.IUserService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private IUserService userService;
	
	private Logger log = Logger.getLogger(getClass());
	
	@GetMapping("/test")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity test(){
	
		return new ResponseEntity("It worked !", HttpStatus.OK);
	}
	
	@RequestMapping(value="/test2", method= RequestMethod.GET)
	public ResponseEntity<?> test2(HttpServletRequest request) {
	  if ( request.isUserInRole("ADMIN") ) {
	 
	    Principal userPrincipal = request.getUserPrincipal();

	    ArrayList<String> groups = 
	          (ArrayList<String>)request.getAttribute("groups");
	    for ( String s : groups) {
	        log.info("part of group: "+s);
	    }
	  }
	  return new ResponseEntity<>("ok !!", HttpStatus.OK);
	}
}
