package org.warungikan.api.model;

public class GeoCodeDistance {

	private StartLocation start_location;
	private Distance distance;
	
	
	
	public StartLocation getStart_location() {
		return start_location;
	}

	public void setStart_location(StartLocation start_location) {
		this.start_location = start_location;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public class StartLocation{
		private String lng;
		private String lat;
		public String getLng() {
			return lng;
		}
		public void setLng(String lng) {
			this.lng = lng;
		}
		public String getLat() {
			return lat;
		}
		public void setLat(String lat) {
			this.lat = lat;
		}
		
	}
	
	public class Distance{
		private String text;
		private String value;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		
		
	}
}
