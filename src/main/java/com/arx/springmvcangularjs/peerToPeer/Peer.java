package com.arx.springmvcangularjs.peerToPeer;

import java.util.UUID;

import org.springframework.context.support.GenericXmlApplicationContext;

public class Peer {
	private String id;
	private String ip;
	private GenericXmlApplicationContext context;
	private Object[][] similarDatasets;
	
	/**
	 * Constructors 
	 */
	public Peer(){
		this.id = UUID.randomUUID().toString();
	}
	
	public Peer(String id){
		this.id = id;
	}

	/**
	 * Getters and Setters
	 */
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GenericXmlApplicationContext getContext() {
		return context;
	}

	public void setContext(GenericXmlApplicationContext context) {
		this.context = context;
	}

	public Object[][] getSimilarDatasets() {
		return similarDatasets;
	}

	public void setSimilarDatasets(Object[][] similarDatasets) {
		this.similarDatasets = similarDatasets;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
