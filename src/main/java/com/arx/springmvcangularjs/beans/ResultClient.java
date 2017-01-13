package com.arx.springmvcangularjs.beans;

import com.arx.springmvcangularjs.beans.parameter.ParameterResult;

public class ResultClient {
	String id;
	Object[][] result;
	ParameterResult parameters;
	
	public ResultClient() {}
	
	public ResultClient(Object[][] result, String datasetId){
		this.id = datasetId;
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object[][] getResult() {
		return result;
	}

	public void setResult(Object[][] result) {
		this.result = result;
	}

	public ParameterResult getParameters() {
		return parameters;
	}

	public void setParameters(ParameterResult parameters) {
		this.parameters = parameters;
	}

}
