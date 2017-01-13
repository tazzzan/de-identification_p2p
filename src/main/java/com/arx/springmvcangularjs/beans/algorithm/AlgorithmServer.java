package com.arx.springmvcangularjs.beans.algorithm;

import org.deidentifier.arx.ARXConfiguration;

@SuppressWarnings("serial")
public class AlgorithmServer extends Algorithm {

	AlgorithmType algorithmType;	
	ARXConfiguration config;
	
	public AlgorithmServer(){
		super();
	}
	
	public AlgorithmServer(String usedDefinitionId) {
		super(usedDefinitionId);
		this.config = ARXConfiguration.create();
	}

	public AlgorithmType getAlgorithmType() {
		return algorithmType;
	}
	public void setAlgorithmType(AlgorithmType algorithmType) {
		this.algorithmType = algorithmType;
	}
	public ARXConfiguration getConfig() {
		return config;
	}
	public void setConfig(ARXConfiguration config) {
		this.config = config;
	} 
}
