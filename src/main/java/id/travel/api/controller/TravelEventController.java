package id.travel.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.warungikan.db.model.TravelEvent;
import org.warungikan.db.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import id.travel.api.model.BasicResponse;
import id.travel.api.service.ITravelService;

@RestController
@RequestMapping("/travel")
public class TravelEventController {
	
	@Autowired
	private ITravelService travelService;
	
	@JsonView(View.Public.class)
	@PostMapping("/list/{agent-id}")
	public ResponseEntity<List> listTravel(@PathVariable("agent-id") Long agentId){
		List<TravelEvent> events = travelService.list(agentId);
		return new ResponseEntity<List>(events, HttpStatus.OK);
		
	}
	
	@JsonView(View.Public.class)
	@PostMapping("/add")
	public ResponseEntity<BasicResponse> add(TravelEvent event){
		travelService.addEvent(event);
		return new ResponseEntity<BasicResponse>(new BasicResponse("Event is successfully created", "SUCCESS", event.getName()), HttpStatus.OK);
	}
}
