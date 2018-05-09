package org.warungikan.api.model.response;

import org.warungikan.db.model.User;

public class AgentStock {

	private User user;
	private String transport_price_per_km;
	private String total_km;
	
	public AgentStock(User user, String transport_price_per_km, String totalKm) {
		setUser(user);
		setTransport_price_per_km(transport_price_per_km);
	}
	
	public AgentStock() {}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTransport_price_per_km() {
		return transport_price_per_km;
	}
	public void setTransport_price_per_km(String transport_price_per_km) {
		this.transport_price_per_km = transport_price_per_km;
	}

	public String getTotal_km() {
		return total_km;
	}

	public void setTotal_km(String total_km) {
		this.total_km = total_km;
	}
	
	
	
}
