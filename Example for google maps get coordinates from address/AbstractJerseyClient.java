package com.clubber.rest.client;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class AbstractJerseyClient {
	
	public String get(String url) {
		Client client = Client.create();
		WebResource web_resource = client.resource(url);
		ClientResponse response = web_resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		Integer status = response.getStatus();
		if(status != 200){
			try {
				throw new Exception("Error GET request - response status: " + status);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response.getEntity(String.class);
	}
	
	public String post(String url, String json) {
		Client client = Client.create();
		WebResource web_resource = client.resource(url);
		ClientResponse response = web_resource.accept(MediaType.APPLICATION_JSON).type("application/json").post(ClientResponse.class, json);
		Integer status = response.getStatus();
		if(status != 200){
			try {
				throw new Exception("Error POST request - response status: " + status);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response.getEntity(String.class);
	}
}
