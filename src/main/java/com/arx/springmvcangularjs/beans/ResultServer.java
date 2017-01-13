package com.arx.springmvcangularjs.beans;

import org.deidentifier.arx.ARXResult;

import com.arx.springmvcangularjs.beans.parameter.ParameterResult;

public class ResultServer {
	String id;
	ARXResult result;
	ParameterResult parameters;
	
	public ResultServer() {}
	
	public ResultServer(ARXResult result, String datasetId){
		this.id = datasetId;
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public ARXResult getResult(){
		return this.result;
	}

	public void setResult(ARXResult result) {
		this.result = result;
	}

	public ParameterResult getParameters() {
		return parameters;
	}

	public void setParameters(ParameterResult parameters) {
		this.parameters = parameters;
	}

}
