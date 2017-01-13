package com.arx.springmvcangularjs.beans.parameter;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.DefinitionServer;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmServer;

@SuppressWarnings("serial")
public class ParameterServer extends Parameter {
	
	AlgorithmServer algorithmGeneral;
	List<AlgorithmServer> algorithms;
	List<DefinitionServer> definitions;
	
	public ParameterServer() {}
	
	public ParameterServer(String idDataset){
		setIdDataset(idDataset);
		this.algorithms = new ArrayList<AlgorithmServer>();
		this.definitions = new ArrayList<DefinitionServer>();
	}


	public AlgorithmServer getAlgorithmGeneral() {
		return algorithmGeneral;
	}

	public void setAlgorithmGeneral(AlgorithmServer algorithmGeneral) {
		this.algorithmGeneral = algorithmGeneral;
	}

	public List<AlgorithmServer> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<AlgorithmServer> algorithms) {
		this.algorithms = algorithms;
	}

	public List<DefinitionServer> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<DefinitionServer> definitions) {
		this.definitions = definitions;
	}

}
