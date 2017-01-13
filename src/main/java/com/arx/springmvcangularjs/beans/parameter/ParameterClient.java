package com.arx.springmvcangularjs.beans.parameter;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmClient;

@SuppressWarnings("serial")
public class ParameterClient extends Parameter {
	
	private List<AlgorithmClient> algorithms;

    private int kAnonymityValue;
    private double kAnonymityMaxOutliers;
    private List <DefinitionClient> definitions;
	
    private String algorithmType;
    
	public ParameterClient() {}
	
	public ParameterClient(String idDataset){
		setIdDataset(idDataset);
		this.algorithms = new ArrayList<AlgorithmClient>();
		this.definitions = new ArrayList<DefinitionClient>();
	}


	public List<AlgorithmClient> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<AlgorithmClient> algorithms) {
		this.algorithms = algorithms;
	}

	public int getkAnonymityValue() {
		return kAnonymityValue;
	}

	public void setkAnonymityValue(int kAnonymityValue) {
		this.kAnonymityValue = kAnonymityValue;
	}

	public double getkAnonymityMaxOutliers() {
		return kAnonymityMaxOutliers;
	}

	public void setkAnonymityMaxOutliers(double kAnonymityMaxOutliers) {
		this.kAnonymityMaxOutliers = kAnonymityMaxOutliers;
	}

	public List<DefinitionClient> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<DefinitionClient> definitions) {
		this.definitions = definitions;
	}

	public String getAlgorithmType() {
		return algorithmType;
	}

	public void setAlgorithmType(String algorithmType) {
		this.algorithmType = algorithmType;
	}

}
