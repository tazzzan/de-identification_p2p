package com.arx.springmvcangularjs.beans.dataset;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.parameter.ParameterResult;

public class DatasetP2P extends Dataset implements java.io.Serializable {

private static final long serialVersionUID = -1125964321801437740L;

private List<ParameterResult> parametersResults = new ArrayList<ParameterResult>();

	public DatasetP2P(){}
	
	public DatasetP2P(String id){
		getProperties().setId(id);
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ParameterResult> getParametersResults() {
		return parametersResults;
	}

	public void setParametersResults(List<ParameterResult> parametersResults) {
		this.parametersResults = parametersResults;
	}

}
