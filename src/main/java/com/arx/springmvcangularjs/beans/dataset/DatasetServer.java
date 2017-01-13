package com.arx.springmvcangularjs.beans.dataset;

import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.Data;

import com.arx.springmvcangularjs.beans.ResultServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterServer;

public class DatasetServer extends Dataset{
	
	Data data;
	ParameterServer parameters;
	List<ResultServer> results;
	
	SimilarDatasetsList similarDatasets = new SimilarDatasetsList();

	public DatasetServer(){
		
	}
	
	public DatasetServer(String id){
		getProperties().setId(id);
		this.parameters = new ParameterServer(id);
		this.results = new ArrayList<ResultServer>();
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public ParameterServer getParameters() {
		return parameters;
	}

	public void setParameters(ParameterServer parameters) {
		this.parameters = parameters;
	}

	public List<ResultServer> getResults() {
		return results;
	}

	public void setResults(List<ResultServer> results) {
		this.results = results;
	}

	public SimilarDatasetsList getSimilarDatasets() {
		return similarDatasets;
	}

	public void setSimilarDatasets(SimilarDatasetsList similarDatasets) {
		this.similarDatasets = similarDatasets;
	}



}
