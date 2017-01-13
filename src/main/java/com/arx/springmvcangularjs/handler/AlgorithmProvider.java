package com.arx.springmvcangularjs.handler;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.algorithm.Algorithm;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmServer;

/**
 * 'AlgorithmProvider' is required to store and to deliver existing algorithms
 * 
 * addAlgorithm(algorithm)
 * getAllAlgorithms()
 * getAlgorithm(id)
 * 
 * List<Algorithm> algorithms 
 * 
 * @author ilja
 *
 */
public class AlgorithmProvider {

	List<AlgorithmServer> algorithms = new ArrayList<AlgorithmServer>();
	
	//Constructors
	public AlgorithmProvider() {}

	//Functions
	public void addAlgorithm(AlgorithmServer algorithm) {
		getAlgorithms().add(algorithm);
	}
	
	public List<AlgorithmServer> getAllAlgorithms() {
		return getAlgorithms();
	}
	
	public Algorithm getAlgorithm(String id) {
		Algorithm algorithm = null;
		for(int i=0; i<getAlgorithms().size(); i++) {
			if(getAlgorithms().get(i).getId().equals(id)) {
				algorithm = getAlgorithms().get(i);
			}
		}
		return algorithm;
	}

	
	// Getters and Setters

	public List<AlgorithmServer> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<AlgorithmServer> algorithms) {
		this.algorithms = algorithms;
	}	
}
