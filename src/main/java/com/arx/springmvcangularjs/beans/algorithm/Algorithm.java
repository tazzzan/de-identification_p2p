package com.arx.springmvcangularjs.beans.algorithm;

import java.util.List;
import java.util.UUID;


/**
 * Class 'Algorithm' is literally storage place for anonymization Configuration
 * --> id
 * --> criteria
 *   -> AlgorithmServer
 *   --> config
 *   --> algorithmType (AlgorithmType)
 *  ->AlgorithmClient
 *   --> algorithmType (String)
 * 
 * @author ilja
 *
 */
public abstract class Algorithm implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;
	
	private String id;
	private List<String> criteria;
	private String usedDefinitionId;

	// Constructors
	
	public Algorithm() {}
	
	public Algorithm(String usedDefinitionId) {
		this.id = UUID.randomUUID().toString();
		this.usedDefinitionId = usedDefinitionId;
	}
	
	// Getters and Setters
	
	public List<String> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<String> criteria) {
		this.criteria = criteria;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsedDefinitionId() {
		return usedDefinitionId;
	}

	public void setUsedDefinitionId(String usedDefinitionId) {
		this.usedDefinitionId = usedDefinitionId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}