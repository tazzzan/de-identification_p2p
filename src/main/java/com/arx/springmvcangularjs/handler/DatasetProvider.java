package com.arx.springmvcangularjs.handler;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.column.Column;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.dataset.DatasetP2P;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.property.Properties;


/**
 * 
 * 'DatasetProvider' is used for delivering Datasets and for importing through 'DataImporter.class'
 * - This step is required to be able to give further attributes to the Data as {name, id}
 * - Moreover it delivers the data to further functions as 'DatasetService', 'AlgorithmService' 
 *   where it might be processed
 * - Before delivering the datasets to client they must be transformed to an appropriate format
	 -> Thus, 'DatasetTransformed' is made of: List<String> data | String name | String id
 * - Very important function 'returnDataIn2DArray(data)' allows to use 'dataset.data' into the DOM
 *   -> it is used in 'DatasetTransformer.class' for transforming 'Dataset' to 'DatasetTransformed' 
 *      that is then usable into the DOM
 * 
 * getAllDatasets()
 * getAllDatasetsTransformed()
 * deliverDataset(id)
 * deliverDatasetTransformed(id)
 * deliverDataset(name,true)
 * deliverDatasetTransformed(name,true)
 * 
 * 
 * List<Dataset> datasets
 * DataImporter di
 * 
 * @author ilja
 *
 */

public class DatasetProvider {

	List<DatasetServer> datasetsServer = new ArrayList<DatasetServer>();
	List<DatasetClient> datasetsClient = new ArrayList<DatasetClient>();
	List<DatasetP2P> datasetsP2P = new ArrayList<DatasetP2P>();
	
	// Constructors
	
	public DatasetProvider() {};
	
	// Functions
	
	public void add(DatasetServer ds){
		datasetsServer.add(ds);
	}
	
	public void add(DatasetClient ds){
		datasetsClient.add(ds);
	}
	
	public void add(DatasetP2P ds){
		datasetsP2P.add(ds);
	}
	public List<DatasetServer> getAllDatasetsServer() {
		return datasetsServer;
	}

	public List<DatasetClient> getAllDatasetsClient() {
		return datasetsClient;
	}
	
	public List<DatasetP2P> getAllDatasetsP2P(){
		return datasetsP2P;
	}
	
	// Deliver Dataset if ID is available 
	public DatasetServer deliverDatasetServer(String id) {
		DatasetServer ds = null;
				
		for(int i=0; i<datasetsServer.size(); i++) {
			if(datasetsServer.get(i).getProperties().getId().equals(id)){
				ds = datasetsServer.get(i);
			}
		}

		return ds;
	}
	
	// Deliver Dataset if ID is available 
	public DatasetClient deliverDatasetClient(String id) {
		DatasetClient ds = null;
				
		for(int i=0; i<datasetsClient.size(); i++) {
			if(datasetsClient.get(i).getProperties().getId().equals(id)){
				ds = datasetsClient.get(i);
			}
		}

				
		return ds;
	}
	
	// Deliver Dataset if ID is available 
	public DatasetP2P deliverDatasetP2P(String id) {
		DatasetP2P ds = null;
				
		for(int i=0; i<datasetsP2P.size(); i++) {
			if(datasetsP2P.get(i).getProperties().getId().equals(id)){
				ds = datasetsP2P.get(i);
			}
		}
				
		return ds;
	}

	// Deliver Dataset if ID not available | note: 'noID' can be true or false !! 
	public DatasetServer deliverDatasetServer(String name, Boolean noID) {
		DatasetServer ds = new DatasetServer();
		
		for(int i=0; i<datasetsServer.size(); i++) {
			if(datasetsServer.get(i).getProperties().getName().equals(name)){
				ds = datasetsServer.get(i);
			}
		}
		
		return ds;
	}

	// Deliver Dataset if ID not available | note: 'noID' can be true or false !! 
	public DatasetClient deliverDatasetClient(String name, Boolean noID) {
		DatasetClient ds = new DatasetClient();
		
		for(int i=0; i<datasetsClient.size(); i++) {
			if(datasetsClient.get(i).getProperties().getName().equals(name)){
				ds = datasetsClient.get(i);
			}
		}
		
		return ds;
	}

	// Deliver Dataset if ID not available | note: 'noID' can be true or false !! 
	public DatasetP2P deliverDatasetP2P(String name, Boolean noID) {
		DatasetP2P ds = new DatasetP2P();
		
		for(int i=0; i<datasetsP2P.size(); i++) {
			if(datasetsP2P.get(i).getProperties().getName().equals(name)){
				ds = datasetsP2P.get(i);
			}
		}

		
		return ds;
	}
	
	public DatasetP2P deliverSimilarDatasetClient(DatasetClient dataset, String similarId){
		for(DatasetP2P similarDataset : dataset.getSimilarDatasets()){
			if(similarDataset.getProperties().getId().equals(similarId)){
				return similarDataset;
			}
		}
		return null;
	}
	
	public int numberOfSimilarDS(ParameterClient parameters){
    	/** Get number of similar datasets in system
    	 */
    	int counter = 0;
    	for(DatasetServer existingDataset : datasetsServer){    		   	
    		for(DatasetP2P datasetInNetwork: existingDataset.getSimilarDatasets()){
    			if(datasetInNetwork.getProperties().getId().equals(parameters.getIdDataset())){
    				counter++;
    			}
    		}
    	}
    	return counter;
	}
	
	public String[][] getSimilarDatasets(Properties properties){
		String[][] similarDatasets = new String[datasetsServer.size()][2];
		
		for(int i=0; i<similarDatasets.length; i++){	
			int counter = 0;
	    	/** Find a similar dataset to received properties
	    	 */
	    	DatasetServer datasetLocal = datasetsServer.get(i);    		
	    	for(Column columnLocal : datasetLocal.getProperties().getColumns()){
	        	for(Column columnRemote : properties.getColumns()){
	        			
	        		if(columnLocal.getName().equals(columnRemote.getName())  && 
	        		   columnLocal.getColumnType().equals(columnRemote.getColumnType()))
	        		{
	        			counter++;
	            		/** If similar then
	            		*/
	        			if(counter == properties.getColumnNum()){
	        				/** Create a similar dataset
	        				 */
	        				DatasetP2P datasetInNetwork = new DatasetP2P();
	        				datasetInNetwork.getProperties().setPeerId(properties.getPeerId());
	        				datasetInNetwork.getProperties().setId(properties.getId());
	        				/** Save in SimilarDatasetsList of matched dataset		
	        				 */
	        				datasetLocal.getSimilarDatasets().add(datasetInNetwork);
	        		    	deliverDatasetClient(datasetLocal.getProperties().getId()).getSimilarDatasets().add(datasetInNetwork);
	        					
	        				/** Create reply to client
	        				*/
	        				similarDatasets[i][0] = properties.getId(); // id of remote data set
	        				similarDatasets[i][1] = datasetLocal.getProperties().getId(); // id of local data set
	        		    		
	        				counter=0;
	        			}
	        		}
	    		}
	    	}		   
    	}

    	return similarDatasets;
	}
	// Getters and Setters

	public List<DatasetServer> getDatasetsServer() {
		return datasetsServer;
	}

	public void setDatasetsServer(List<DatasetServer> datasetsServer) {
		this.datasetsServer = datasetsServer;
	}

	public List<DatasetClient> getDatasetsClient() {
		return datasetsClient;
	}

	public void setDatasetsClient(List<DatasetClient> datasetsClient) {
		this.datasetsClient = datasetsClient;
	}

	public List<DatasetP2P> getDatasetsP2P() {
		return datasetsP2P;
	}

	public void setDatasetsP2P(List<DatasetP2P> datasetsP2P) {
		this.datasetsP2P = datasetsP2P;
	}



	
}
