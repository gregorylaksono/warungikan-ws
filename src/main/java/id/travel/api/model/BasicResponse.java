package id.travel.api.model;

import java.io.Serializable;

public class BasicResponse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3647242429304182135L;
	private String message;
	private String code;
	private String info;
	public BasicResponse(String message, String code, String info){
		setMessage(message);
		setCode(code);
		setInfo(info);
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
}
