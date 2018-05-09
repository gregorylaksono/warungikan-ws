package org.warungikan.api.model.response;

import org.warungikan.db.model.User;

public class AgentStock {

	private User user;
	private String transport_price_per_km;
	private String total_distance;
	private String latitude;
	private String longitude;
	
	public AgentStock(User user, String transport_price_per_km, String totalDistance, String latitude, String longitude) {
		setUser(user);
		setTransport_price_per_km(transport_price_per_km);
		setLatitude(latitude);
		setLongitude(longitude);
		setTotal_distance(totalDistance);
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

	public String getTotal_distance() {
		return total_distance;
	}

	public void setTotal_distance(String total_distance) {
		this.total_distance = total_distance;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	
	
	
}
