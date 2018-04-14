package id.travel.api.model;

import java.io.Serializable;

public class UserLogin implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -610850993087447021L;
	private String username;
	private String password;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
