package id.travel.api.service;

import java.util.List;

import org.warungikan.db.model.TravelEvent;

public interface ITravelService {

	
	public TravelEvent addEvent(TravelEvent event);
	
	public List<TravelEvent> list(Long agentId);
}
