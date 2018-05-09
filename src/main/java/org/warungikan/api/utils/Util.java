package org.warungikan.api.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.warungikan.api.model.GeoCodeDistance;

import com.google.gson.Gson;

public class Util {

	
	public static GeoCodeDistance getDistance(String origin, String destination) {
		RestTemplate t = new RestTemplate();
		try {
			String url = Constant.GOOGLE_DIRECTION_URL+"origin="+origin+"&destination="+destination+"&key="+Constant.GMAP_API_KEY;
			ResponseEntity<String> response = t.exchange(new URI(url), HttpMethod.GET,null,  String.class);
			String body = response.getBody();
			GeoCodeDistance geoCode = parse(body);
			return geoCode;
		} catch (RestClientException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	private static GeoCodeDistance parse(String body) {
		try {
			JSONObject mainJSON=new JSONObject(body);
			JSONArray test = mainJSON.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
			String a = test.get(0).toString();
			GeoCodeDistance geoCodeDistance = new Gson().fromJson(a, GeoCodeDistance.class);
			return geoCodeDistance;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	}
