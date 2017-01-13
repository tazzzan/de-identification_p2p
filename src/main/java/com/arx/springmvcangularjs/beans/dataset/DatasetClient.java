package com.arx.springmvcangularjs.beans.dataset;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.ResultClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;

public class DatasetClient extends Dataset{
	
	Object[][] data;	
	ParameterClient parameters;
	List<ResultClient> results;
	
	SimilarDatasetsList similarDatasets = new SimilarDatasetsList();
	
	public DatasetClient(){
	}

	public DatasetClient(String id){
		getProperties().setId(id);
		this.parameters = new ParameterClient(id);
		this.results = new ArrayList<ResultClient>();
	}
	
	public Object[][] getData() {
		return data;
	}

	public void setData(Object[][] data) {
		this.data = data;
	}

	public ParameterClient getParameters() {
		return parameters;
	}

	public void setParameters(ParameterClient parameters) {
		this.parameters = parameters;
	}

	public List<ResultClient> getResults() {
		return results;
	}

	public void setResults(List<ResultClient> results) {
		this.results = results;
	}

	public SimilarDatasetsList getSimilarDatasets() {
		return similarDatasets;
	}

	public void setSimilarDatasets(SimilarDatasetsList similarDatasets) {
		this.similarDatasets = similarDatasets;
	}

}
