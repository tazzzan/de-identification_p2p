package com.arx.springmvcangularjs.service;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Service;

import com.arx.springmvcangularjs.beans.algorithm.AlgorithmType;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.dataset.DatasetP2P;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterResult;
import com.arx.springmvcangularjs.handler.AlgorithmProvider;
import com.arx.springmvcangularjs.handler.DatasetProvider;
import com.arx.springmvcangularjs.handler.DatasetUpdater;
import com.arx.springmvcangularjs.handler.FolderReader;
import com.arx.springmvcangularjs.managers.AlgorithmManager;
import com.arx.springmvcangularjs.managers.DatasetManager;
import com.arx.springmvcangularjs.peerToPeer.Peer;
import com.arx.springmvcangularjs.peerToPeer.PeersManager;

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

@Service("DatasetService")
public class DatasetServiceImpl implements DatasetService {
	
	Peer activePeer = new Peer();
	PeersManager manager = new PeersManager();
	
	DatasetProvider dp = new DatasetProvider();
	DatasetManager dm = new DatasetManager(dp, manager);
	AlgorithmProvider ap = new AlgorithmProvider();
	AlgorithmManager am = new AlgorithmManager(ap, dm);
	DatasetUpdater update = new DatasetUpdater(dp, dm);
	
	/**
	 * CONSTRUCTOR
	 * -> open ports for server-side sockets of peer
	 * -> create connection to peers in list
	 */
	public DatasetServiceImpl(){
		manager.createNewLocalPeerConnection(activePeer);
	}
	
	/**	
	 * METHODS
	 */
	
	/** || DATASET OPERATIONS:
	 */

	@Override
	public String addNewDataset(String path, String name) {
		return dm.createNewDataset(path, name, activePeer.getId());
	}
	
	@Override
	public DatasetClient getDataset(String id) {
		return dp.deliverDatasetClient(id);
	}
	
	@Override
	public List<DatasetClient> getAllDatasets() {
		return dp.getAllDatasetsClient(); // return dataset_client version
	}
	
	@Override
	public void anonymizeDataset(String id) {
		DatasetServer dataset = dp.deliverDatasetServer(id);
		dm.anonymizeDataset(dataset, dp.deliverDatasetClient(id).getParameters());
    	update.updateParam(dataset.getParameters());
	}

	
	@Override
	public void removeDataset(String id) {
		dm.removeDataset(id);
	}
	
	@Override
	public void removeAllDatasets() {
		dm.removeAllDatasets();
	}
	
	/** || DEFINITION OPERATIONS:
	 */
	
	@Override
	public void submitNewDefinition(ParameterClient parameters){
		dm.updateAttributeTypes(parameters);
    	update.updateParam(dp.deliverDatasetServer(parameters.getIdDataset()).getParameters());
	}
	
	@Override
	public void selectDefinition(String datasetId, String definitionId){
		dm.setDefinitionARXForDataset(datasetId, definitionId);
		update.updateParam(dp.deliverDatasetServer(datasetId).getParameters());
	}
	
	@Override
	public void removeDefinition(String datasetId, String definitionId) {
		dm.removeDefinition(datasetId, definitionId);
		update.updateParam(dp.deliverDatasetServer(datasetId).getParameters());
	}

	/** || ALGORITHM OPERATIONS:
	 */
	
	@Override
	public void createAnonymizationAlgorithm(String datasetId, AlgorithmType algorithmType, ParameterClient parameters){
		parameters.setAlgorithmType(algorithmType.toString());
		
		DatasetServer dataset = dp.deliverDatasetServer(datasetId);
		DatasetClient datasetClient = dp.deliverDatasetClient(datasetId);

		datasetClient.setParameters(parameters);
		
		am.createAnonymizationAlgorithm(dataset, algorithmType, parameters);
		if(algorithmType == AlgorithmType.getL_DIVERSITY() ||
		   algorithmType == AlgorithmType.getT_CLOSENESS()) {
			am.createAnonymizationAlgorithm(dataset, AlgorithmType.getK_ANONYMITY(), parameters);
		}

    	/** Make Utility and Risk Analysis on each remote peer
		 *  send parameters
		 * ( receive parametersAnalysisList )
		 */
		 
		for(Peer peer : manager.getPeersList()){
			String[][] reply = manager.sendParametersToPeers(peer.getId(), parameters);
			
			/** The local peer gets its datasetInNetwork updated
			 *  server-version: update in list of existingDataset
			 *  client-version: add 
			 */
			for(int i=0; i<reply.length; i++){
				for(DatasetP2P datasetInNetwork : dataset.getSimilarDatasets()){
					
					if(datasetInNetwork.getProperties().getId().equals(reply[i][0]))
					{
						ParameterResult parameterResult = am.createParameterResult(parameters);
						parameterResult.setInformationLoss(reply[i][1]);
						
			    		datasetInNetwork.getParametersResults().add(parameterResult);
			    		System.out.println("remote dataset found in system and added information loss and parameters");

			    		for(ParameterResult pr: datasetInNetwork.getParametersResults()){
			    			System.out.println(pr.getkAnonymityValue() + "::: kanony value");
			    		}
						System.out.println("peers reply: "+datasetInNetwork.getProperties().getPeerId());
						System.out.println("reply of server {inf.loss}: " + parameterResult.getInformationLoss());
						System.out.println("reply of server {dataset.id}: "+ datasetInNetwork.getProperties().getId());
					}					
				}
			}
		}	
		update.updateParam(dp.deliverDatasetServer(datasetId).getParameters());
	}
	
	@Override 
	public void updateAlgorithm(String algorithmId, ParameterClient parameters){
		am.updateAlgorithm(dp.deliverDatasetServer(parameters.getIdDataset()), algorithmId, parameters);
		update.updateParam(dp.deliverDatasetServer(parameters.getIdDataset()).getParameters());

	}

	@Override
	public void selectAlgorithm(String datasetId, String algorithmId){
		am.setAlgorithm(algorithmId, dp.deliverDatasetServer(datasetId));
		update.updateParam(dp.deliverDatasetServer(datasetId).getParameters());
	}

	@Override
	public List<String> getAlgorithmCriteria(String id){
		return am.getSavedAlgorithmCriteria(dp.deliverDatasetServer(id));
	}
	
	/** || PARAMETERS OPERATIONS:
	 */ 

	@Override
	public ParameterClient getParameters(String datasetId){
		return dp.deliverDatasetClient(datasetId).getParameters();
	}
	
	/** || OTHER OPERATIONS:
	*/
		
	@Override
	public List<String> getAttributeTypeOptions(){
		return dm.getAttributeTypesForDefinition();
	}
	
	@Override
	public List<File> readFilesOnSystem(String path) {
		FolderReader fr = new FolderReader();
		return fr.returnFildesInFolder(path);
	}
		
	/** TO EDIT
	 */
	@Override
	public void createHierarchyAutoNoImport(List<String> creationParams){
		dm.createHierarchy(creationParams.get(0),
					   	   creationParams.get(1),
					   	   0,0,0,
					   	   creationParams.get(2));
	}
	
	@Override
	public void createHierarchyMan(List<String> creationParams){
		dm.createHierarchy(creationParams.get(0),
						   creationParams.get(1),
						   Integer.parseInt(creationParams.get(3)),
						   Integer.parseInt(creationParams.get(4)),
						   Integer.parseInt(creationParams.get(5)),
						   creationParams.get(2));
	}

	/**
	 * Getters and Setters
	 */	
	
	public DatasetProvider getDp() {
		return dp;
	}

	public void setDp(DatasetProvider dp) {
		this.dp = dp;
	}

	public AlgorithmProvider getAp() {
		return ap;
	}

	public void setAp(AlgorithmProvider ap) {
		this.ap = ap;
	}

	public AlgorithmManager getAm() {
		return am;
	}

	public void setAm(AlgorithmManager am) {
		this.am = am;
	}

	public DatasetManager getDm() {
		return dm;
	}

	public void setDm(DatasetManager dm) {
		this.dm = dm;
	}

	public Peer getActivePeer() {
		return activePeer;
	}

	public void setActivePeer(Peer activePeer) {
		this.activePeer = activePeer;
	}
	public PeersManager getManager() {
		return manager;
	}
	public void setManager(PeersManager manager) {
		this.manager = manager;
	}
}
