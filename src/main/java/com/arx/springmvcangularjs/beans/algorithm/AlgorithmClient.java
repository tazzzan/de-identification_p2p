package com.arx.springmvcangularjs.beans.algorithm;

@SuppressWarnings("serial")
public class AlgorithmClient extends Algorithm {
	private String algorithmType;

	public AlgorithmClient() {
		super();
	}
	
	public AlgorithmClient(String id, String usedDefinitionId){
		super(usedDefinitionId);
		setId(id);;
	}
	
	public String getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}
	
}
