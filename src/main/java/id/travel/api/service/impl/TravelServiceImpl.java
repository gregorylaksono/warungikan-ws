package id.travel.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.warungikan.db.model.Agent;
import org.warungikan.db.model.TravelEvent;
import org.warungikan.db.repository.AgentRepository;
import org.warungikan.db.repository.TravelRepository;

import id.travel.api.service.ITravelService;

public class TravelServiceImpl implements ITravelService {

	@Autowired
	private TravelRepository travelRepository;
	
	@Autowired
	private AgentRepository agentRepository;
	
	@Override
	public TravelEvent addEvent(TravelEvent event) {
		travelRepository.save(event);
		return event;
	}

	@Override
	public List<TravelEvent> list(Long agentId) {
		Agent agent = agentRepository.findOne(agentId);
		
		return travelRepository.findByAgent(agent);
	}

}
