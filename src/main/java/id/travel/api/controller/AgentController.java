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
import org.warungikan.db.model.Agent;
import org.warungikan.db.model.User;
import org.warungikan.db.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.IAgentService;

@RestController
@RequestMapping("/agent")
public class AgentController {

	@Autowired
	private IAgentService agentService;
	
	@PostMapping("/register/user/{userId}")
	@JsonView(View.Public.class)
	public ResponseEntity<BasicResponse> register(@RequestBody Agent agent, @PathVariable("userId") Long userId){
	
		Agent t = agentService.registerAgent(userId, agent.getName());
		return new ResponseEntity<BasicResponse>(new BasicResponse("Agent successfuly registered", "SUCCES", t.getName()), HttpStatus.OK);
	}
	
	@GetMapping("/list")
	@JsonView(View.Public.class)
	public ResponseEntity<List> getAgent(){
	
		List<Agent> t = agentService.getAllAgents();
		return new ResponseEntity<List>(t, HttpStatus.OK);
	}
	
	@GetMapping("/{agent-id}")
	@JsonView(View.Public.class)
	public ResponseEntity<Agent> getOne(@PathVariable("agent-id") Long agentId){
		Agent agent = agentService.getAgentByUserId(agentId);
		return new ResponseEntity<Agent>(agent, HttpStatus.OK);
	}
}
