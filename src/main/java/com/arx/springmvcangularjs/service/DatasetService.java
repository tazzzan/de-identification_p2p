package com.arx.springmvcangularjs.service;

import java.io.File;
/**
 * 'DatasetService' handles with data sets an its attributes:
 * Parameters ( Algorithm | Definition ) , Properties
 * 
 * It is the initial loaded instance
 * when it comes to communication between the server and client
 * 
 * || DATASET OPERATIONS:
 * addNewDataset()
 * getDataset()
 * getAllDatasets()
 * anonymizeDataset()
 * removeDataset()
 * removeAllDatasets()
 * 
 * || DEFINITION OPERATIONS:
 * submitNewDefinition()
 * selectDefinition()
 * removeDefinition()
 * 
 * || ALGORITHM OPERATIONS:
 * createAnonymizationAlgorithm()
 * updateAlgorithm()
 * selectAlgorithm()
 * getAlgorithmCriteria()
 * 
 * || PARAMETERS OPERATIONS:
 * getParameters()
 *   
 * || OTHER OPERATIONS:
 * getAttributeTypeOptions
 * readFilesOnSystem
 * 
 * 
 * >> DatasetService attributes:  
 * Peer activePeer;
 * PeersManager manager;
 * 
 * DatasetProvider dp
 * AlgorithmProvider ap
 * AlgorithmManager am
 * DatasetManager dm
 * 
 * @author Ilja Lichtenberg
 *
 */

import java.util.List;

import com.arx.springmvcangularjs.beans.algorithm.AlgorithmType;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;

public interface DatasetService {

	public String addNewDataset(String path, String name);
	public DatasetClient getDataset(String id);
	public List<DatasetClient> getAllDatasets();
	public void anonymizeDataset(String id);
	public void removeDataset(String id);
	public void removeAllDatasets();
	

	public void submitNewDefinition(ParameterClient parameters);
	public void selectDefinition(String datasetId, String definitionId);
	public void removeDefinition(String datasetId, String definitionId); 
	
	public void createAnonymizationAlgorithm(String datasetId, AlgorithmType algorithmType, ParameterClient parameters);
	public void updateAlgorithm(String algorithmId, ParameterClient parameters);
	public void selectAlgorithm(String datasetId, String algorithmId);
	public List<String> getAlgorithmCriteria(String id);
	
	public ParameterClient getParameters(String datasetId);
	
	public List<String> getAttributeTypeOptions();
	public List<File> readFilesOnSystem(String path);
	public void createHierarchyAutoNoImport(List<String> creationParams);
	public void createHierarchyMan(List<String> creationParams);

}
