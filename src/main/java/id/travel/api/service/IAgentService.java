package id.travel.api.service;

import java.util.List;

import org.warungikan.db.model.Agent;

public interface IAgentService {

	public Agent registerAgent(Long userId, String agentName);
	public Agent getAgentByUserId(Long userId);
	public List<Agent> getAllAgents();
}
