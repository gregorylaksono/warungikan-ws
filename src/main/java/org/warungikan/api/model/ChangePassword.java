package org.warungikan.api.model;

import java.io.Serializable;

public class ChangePassword implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2651802822885141712L;
	private String password;
	private String newPassword;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	
}
