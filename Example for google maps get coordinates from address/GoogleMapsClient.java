package com.clubber.rest.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMethod;

import com.clubber.facebook.db.model.Location;

public class GoogleMapsClient extends AbstractJerseyClient{
	
	private final String APP_SERVER_URL = "http://maps.googleapis.com/maps";
	private final String API_VERSION = "api";
	
	private String get(String address, String reference, String mediaType) {
		String shortUrl = APP_SERVER_URL + "/" + API_VERSION + "/" + reference + "/" + mediaType + "?address=";
		log(RequestMethod.GET, shortUrl + address);
		String json = "{}";
		try {
			String url = shortUrl + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
			json = super.get(url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if(!FacebookFilters.isValidJson(json)){
			return null;
		}
		return json;
	}
	
	public Location getLocation(String address) {
		String json = this.get(address, "geocode", "json");
		Place place = new Place();
		try {
			Location location = new Location();
			JSONObject jsonLocation, jsonObject = new JSONObject(json);
			JSONArray jsonResults = jsonObject.getJSONArray("results");
			if(jsonResults == null){
				return null;
			} else if (jsonResults.length() == 0) {
				return null;
			}
			JSONObject jsonResult = jsonResults.getJSONObject(0);
			JSONArray jsonAddressComponents = jsonResult.getJSONArray("address_components");
			
			jsonLocation = jsonResult.getJSONObject("geometry").getJSONObject("location");
		    location.setLatitude(Float.parseFloat(jsonLocation.getString("lat")));
		    location.setLongitude(Float.parseFloat(jsonLocation.getString("lng")));
		   
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return location;
	}
	
	private void log(RequestMethod requestMethod , String url) {
		System.out.println(requestMethod + ": " + url);
	}
}