package id.travel.api.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.warungikan.db.model.Agent;
import org.warungikan.db.model.User;
import org.warungikan.db.repository.AgentRepository;
import org.warungikan.db.repository.UserRepository;

import id.travel.api.service.IAgentService;

@Service
public class AgentServiceImpl implements IAgentService {

	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	public Agent registerAgent(Long userId, String agentName) {
		User user = userRepository.findOne(userId);
		Agent a = new Agent();
		a.setUser(user);
		a.setName(agentName);
		agentRepository.save(a);
		return a;
	}

	@Override
	public Agent getAgentByUserId(Long userId) {
		User user = userRepository.findOne(userId);
		Agent agent = agentRepository.findByUser(user);
		return agent;
	}

	@Override
	public List<Agent> getAllAgents() {
		return agentRepository.findAll();
	}

}
