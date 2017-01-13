package com.arx.springmvcangularjs.managers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.DefinitionServer;
import com.arx.springmvcangularjs.beans.ResultClient;
import com.arx.springmvcangularjs.beans.ResultServer;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmType;
import com.arx.springmvcangularjs.beans.column.ColumnClient;
import com.arx.springmvcangularjs.beans.column.ColumnServer;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.dataset.DatasetP2P;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterResult;
import com.arx.springmvcangularjs.beans.parameter.ParameterServer;
import com.arx.springmvcangularjs.handler.DataImporter;
import com.arx.springmvcangularjs.handler.DatasetProvider;
import com.arx.springmvcangularjs.handler.Generalizer;
import com.arx.springmvcangularjs.handler.Transformer;
import com.arx.springmvcangularjs.handler.UtilityAnalyzer;
import com.arx.springmvcangularjs.peerToPeer.PeersManager;

/**
 * 'DatasetManager' is important for every dealing with datasets
 * Its primary objects are:
 *  - to create a new 'Dataset'
 *  - to anonymize the 'Dataset' by an appropriate algorithm
 *  - to set Definition/Hierarchy for 'Dataset' (in this case Generalization configurations
 * 
 * Methods:
 * || DATASET OPERATIONS
 * createNewDataset
 * anonymizeDataset
 * anonymizeDatasetByParameters
 * removeDataset
 * removeAllDatasets
 * 
 * || DEFINITION OPERATIONS
 * createDefinitionServer
 * updateAttributeTypes
 * getDefinition
 * getDefinitionClient
 * setDefinitionARXForDataset
 * setDefinitionARXForDataset
 * existDefinitionServer
 * removeDefinition
 * 
 * || HIERARCHY OPERATIONS
 * createHierarchy
 * getHierarchy
 * getColumn
 * 
 * || OTHERS
 * addAnonymizedData
 * getAttributeTypesForDefinition
 * 
 * 
 * Members: 
 * 
 * DatasetProvider dp
 * PeersManager manager
 * AlgorithmManager am
 * DataImporter di
 * Generalizer gen
 * 
 * @author Ilja Lichtenberg
 *
 */

public class DatasetManager {
	
	DatasetProvider dp;
	PeersManager manager;
	
	AlgorithmManager am = new AlgorithmManager();
	DataImporter di = new DataImporter();
	Generalizer gen = new Generalizer();
	
	/**
	 * Constructors
	 */
	
	public DatasetManager() {};
	
	public DatasetManager(DatasetProvider dp, PeersManager manager) {
		this.dp = dp;
		this.manager = manager;
	}
	
	/**
	 * Methods
	 */
	
	/**
	 * || DATASET OPERATIONS
	 */
	
	public String createNewDataset(String path, String name, String localPeerId) {	
		if(name==null){
			File f = new File(path);
			name = f.getName();
			String[] parts = name.split(Pattern.quote("."));
			name = parts[0];
		}

		DatasetServer dataset =  di.importDataIntoNewDataset(path, name, dp);
		dataset.getProperties().setPeerId(localPeerId);
		
		/**Check on each remote peer from peersList
		 * If analog datasets exist it will be attached by manager
		 */
		manager.checkForSimilarDatasetsInNetwork(dataset, dp);
		
		return dataset.getProperties().getId();
	}
		
	/** Anonymization Execution
	 * 
	 * Before anonymization method can be invoked MUST HAPPEN:
	 *  --> setDefinitionARXForDataset(datasetId, definitionId)
	 *  --> setAlgorithm(datasetId, algorithmId)
	 */
	public DatasetServer anonymizeDataset(DatasetServer dataset, ParameterClient param) {
		ARXAnonymizer anonymizer = new ARXAnonymizer();

		/**Prepare the ARX Anonymizer with data and config
		 */
		Data data = dataset.getData();
		ARXConfiguration config = dataset.getParameters().getAlgorithmGeneral().getConfig();
    	
    	/** Anonymization 
    	 */
    	try {
        	ARXResult result = anonymizer.anonymize(data, config);
        	UtilityAnalyzer ua = new UtilityAnalyzer();
        	
        	if(result.isResultAvailable()){
        		addAnonymizedData(result, dataset, param);
        		
        		dataset.getResults().get(dataset.getResults().size()-1).getParameters().setInformationLoss(ua.getInfoInformationLossValue(result));
        		
        		ua.getInfoInformationLoss(result);
        		result.getOutput(false).save("datasets/anonymized/"+dataset.getProperties().getName()+"_anonymized.csv", ',' );
        		
        		data.getHandle().release();
        	}
        }
    	catch (IOException e) {throw new RuntimeException(e);}

    	return dataset;
	}
	
	/** Anonymization Execution By Parameters
	 */
	public String[][] anonymizeDatasetByParameters(ParameterClient parameters){
		/** Get all similar datasets from all local datasets
		 *  look for any matches with the data set id from received param
		 */    
    	String[][] similarDatasets = new String[dp.numberOfSimilarDS(parameters)][2];
    	int counter = 0;
    	for(DatasetServer existingDataset : dp.getAllDatasetsServer()){ 
    		
    		for(DatasetP2P datasetInNetwork: existingDataset.getSimilarDatasets()){
    			if(datasetInNetwork.getProperties().getId().equals(parameters.getIdDataset()))
    			{    		
    				/** Don't use the existing data set to anonymize
    				 *  Create a new one: similarDatasetTemp
    				 */
    				DatasetServer similarDatasetTemp = new DatasetServer("noId");
    	        	similarDatasetTemp.setData(existingDataset.getData());
    	        	similarDatasetTemp.setProperties(existingDataset.getProperties());
    	        	similarDatasetTemp.setParameters(new ParameterServer(parameters.getIdDataset()));
    	        	similarDatasetTemp.getParameters().setSettedDefinitionId(parameters.getSettedDefinitionId());
    	        	
    	        	/** Add definitions to similarDatasetTemp from received param
    	        	 */
            		for(DefinitionClient defClient : parameters.getDefinitions()){
                		if(defClient.getId().equals(parameters.getSettedDefinitionId())){
                		
                    		DefinitionServer definition = createDefinitionServer(similarDatasetTemp, defClient, parameters);
                    	
                    	 	/**
                        	 * Adjust ids of column in definition to local column-in-properties id
                        	 */
                			for(int i=0; i<definition.getColumns().size(); i++){
                				String columnId = similarDatasetTemp.getProperties().getColumns().get(i).getId();
                				defClient.getColumns().get(i).setId(columnId);
                				definition.getColumns().get(i).setId(columnId);
                    		}
                    		
                    		/**
                    		 * [if 'quasi_identifying']
                    		 * --> createHierarchy
                    		 */		
                    		for(ColumnServer c: definition.getColumns()){
                    			for(ColumnClient cC: defClient.getColumns()){	
                    				if(c.getId().equals(cC.getId())){
                    					if(c.getAttributeType().equals("quasi_identifying")){
                    						/**
                    						 *  Create 'Hierarchy' based on delivered information from Client
                    						 *  --> save into created 'DefinitionServer' above
                    						 */
                    						String columnName = cC.getName();
                    						int min = cC.getAngularParameters().getGeneralizationMin();
                    						int max = cC.getAngularParameters().getGeneralizationMax();
                    						int biggerThan = cC.getAngularParameters().getGeneralizationBiggerThan();
                    						
                    						gen.createHierarchy(similarDatasetTemp, columnName, min, max, biggerThan, definition);
                    					}
                    				}
                    			}
                    		}
                    		setDefinitionARXForDataset(similarDatasetTemp, definition);
                		}
                	}
            		
                	/** Create Algorithm for similarDatasetTemp from received param
                	 */
                	switch (parameters.getAlgorithmType()) {
                	case "K_ANONYMITY": {
                        am.createAnonymizationAlgorithm(similarDatasetTemp, AlgorithmType.getK_ANONYMITY(), parameters);
                        anonymizeDataset(similarDatasetTemp, parameters);
                		break;
                	}
                	case "T_CLOSENESS": {
                        am.createAnonymizationAlgorithm(similarDatasetTemp, AlgorithmType.getK_ANONYMITY(), parameters);
                        am.createAnonymizationAlgorithm(similarDatasetTemp, AlgorithmType.getT_CLOSENESS(), parameters);
                        anonymizeDataset(similarDatasetTemp, parameters);
                		break;
                	}
                	case "L_DIVERSITY": {
                        am.createAnonymizationAlgorithm(similarDatasetTemp, AlgorithmType.getK_ANONYMITY(), parameters);
                        am.createAnonymizationAlgorithm(similarDatasetTemp, AlgorithmType.getL_DIVERSITY(), parameters);
                        anonymizeDataset(similarDatasetTemp, parameters);
                        break;
                	}
                	}
                	
                	/** Analyze the result of anonymization
                	 */
                	UtilityAnalyzer ua = new UtilityAnalyzer();
                	String informationLoss = ua.getInfoInformationLossValue(similarDatasetTemp.getResults().get(0).getResult());      	
                	
    				/** The local peer gets its datasetInNetwork updated
    				 *  server-version: update in list of existingDataset
    				 *  client-version: add 
    				 */
                	ParameterResult parameterResult = similarDatasetTemp.getResults().get(0).getParameters();
					parameterResult.setInformationLoss(informationLoss);
					
		    		datasetInNetwork.getParametersResults().add(parameterResult);

                	/** The remote peer gets a String[][] List with analysis results
                	 */
                	similarDatasets[counter][0] = existingDataset.getProperties().getId();
    				similarDatasets[counter][1] = informationLoss; // information loss of local data set
    				
    				counter++;
    			}
    		}
    	}
    	
    	return similarDatasets;
	}
	
	public void removeDataset(String id){
		dp.getAllDatasetsServer().remove(dp.deliverDatasetServer(id));
		dp.getAllDatasetsClient().remove(dp.deliverDatasetClient(id));
		dp.getAllDatasetsP2P().remove(dp.deliverDatasetP2P(id));
	}

	public void removeAllDatasets(){
		dp.getAllDatasetsServer().clear();
		dp.getAllDatasetsClient().clear();
		dp.getAllDatasetsP2P().clear();
	}
	
	/**
	 * || DEFINITION OPERATIONS
	 */
	
	public DefinitionServer createDefinitionServer(DatasetServer dataset, DefinitionClient definitionClientReceived, ParameterClient parametersClientReceived){
		List<DefinitionServer> definitionsOld = dataset.getParameters().getDefinitions();
		
		/** Check if definition_client exists on server side
		 *  If exists 
		 *  -> update parameters_client_old with received
		 */
		ParameterClient parametersClientOld;
		if(dp.deliverDatasetClient(parametersClientReceived.getIdDataset()) != null){
			DatasetClient dsClient = dp.deliverDatasetClient(dataset.getProperties().getId());
			dsClient.setParameters(parametersClientReceived);
			parametersClientOld = dsClient.getParameters();
		}
		else{parametersClientOld = null;}
		
		/**Create Definition_Server_new
		 */
		DefinitionServer definitionServerNew = new DefinitionServer();
		definitionServerNew.setDatasetName(dataset.getProperties().getName());

		/** Update its columns from Definition_Client_received
		 */
		for(ColumnClient columnClient: definitionClientReceived.getColumns()){				
			ColumnServer columnServer = new ColumnServer(columnClient.getId());
			columnServer.setName(columnClient.getName());
			columnServer.setAttributeType(columnClient.getAttributeType());
			
			definitionServerNew.getColumns().add(columnServer);
		}		
		
		/** Save definitions_server
		 */
		dataset.getParameters().getDefinitions().add(definitionServerNew);	
		
		/** If Definition_Client_received is the initial one 
		 * 	Than update its id from Definition_Server_new
		 *  Than save in parameter_client_old
		 */
		if(definitionClientReceived.getId().equals("0") && parametersClientOld!=null){
			definitionClientReceived.setId(definitionServerNew.getId());
			parametersClientOld.getDefinitions().add(definitionClientReceived);
		}
		
		/** If Definition_Client_received was already saved as Defintion_Server_old
		 *  Than create Definition_Client_New
		 *  Update its parameters from DefinitionClient_received and Definition_Server_new 		 
		 */
		for(DefinitionServer defininitionServerOld: definitionsOld){
			if(defininitionServerOld.getId().equals(definitionClientReceived.getId())){
				
				DefinitionClient newDefinitionClient = new DefinitionClient(definitionServerNew.getId(), definitionClientReceived.getColumns());
				if(parametersClientOld!=null){
					parametersClientOld.getDefinitions().add(newDefinitionClient);
				}
			}
		}
		
		/** Set created Definition as setted
		 */
		if(parametersClientOld!=null){
			parametersClientOld.setSettedDefinitionId(definitionServerNew.getId());	
		}
		
		dataset.getParameters().setSettedDefinitionId(definitionServerNew.getId());
		
		return definitionServerNew;
	}	
	
	/** Submission of attribute types OR
	 *  Submission of a new definition
	 *  -> new definition will be created from parameter_client
	 */
	public void updateAttributeTypes(ParameterClient parameters) {
		DatasetServer dataset = dp.deliverDatasetServer(parameters.getIdDataset());
		DefinitionClient definitionReceived = null; 
		
		for(DefinitionClient dc : parameters.getDefinitions()){
			if(dc.getId().equals(parameters.getSettedDefinitionId())){
				definitionReceived = dc; 
			}
		}
		
		/** Create definition_server as well as:
		 *  - the import of ColumnClient 
		 *  - setting the new Id for DefinitionClient and Parameters(Client/Server) settedDefinitionId
		 */	
		DefinitionServer definition = createDefinitionServer(dataset, definitionReceived, parameters);
		
		
		/** If 'quasi_identifying'
		 * --> createHierarchy
		 */		
		for(ColumnServer c: definition.getColumns()){
			for(ColumnClient cC: definitionReceived.getColumns()){	
				if(c.getId().equals(cC.getId())){
					if(c.getAttributeType().equals("quasi_identifying")){
						/** Create 'Hierarchy' based on delivered information from Client
						 *  --> save into created 'DefinitionServer' above
						 */
						String columnName = cC.getName();
						int min = cC.getAngularParameters().getGeneralizationMin();
						int max = cC.getAngularParameters().getGeneralizationMax();
						int biggerThan = cC.getAngularParameters().getGeneralizationBiggerThan();
						
						gen.createHierarchy(dataset, columnName, min, max, biggerThan, definition);
					}
				}
			}
		}
		/** Automatically set the created definition as 'default' for generalization
		 */
		gen.setDefinitionARXForDataset(dataset, definition);
	}

	public DefinitionServer getDefinition(String datasetId, String definitionId){
		for(DefinitionServer d: dp.deliverDatasetServer(datasetId).getParameters().getDefinitions()){
			if(d.getId().equals(definitionId)){
				return d;
			}
		}
		return null;
	}
	
	public DefinitionClient getDefinitionClient(String datasetId, String definitionId){
		for(DefinitionClient d: dp.deliverDatasetClient(datasetId).getParameters().getDefinitions()){
			if(d.getId().equals(definitionId)){
				return d;
			}
		}
		return null;
	}
		
	/** ONLY POSSIBLE IF DEFINITION_SERVER WAS CREATED BEFORE
	 *  -> INCLUDES CREATION OF HIERARCHY AND SETTING OF ATTRIBUTE_TYPES FOR COLUMNS 
	 */
	public DefinitionServer setDefinitionARXForDataset(String datasetId, String definitionId){
		DatasetServer dataset = dp.deliverDatasetServer(datasetId);
		DefinitionServer definition = gen.getServerDefinition(dataset, definitionId);
		gen.setDefinitionARXForDataset(dataset, definition);
    	return definition;
	}	
	
	/** ONLY POSSIBLE IF DEFINITION_SERVER WAS CREATED BEFORE
	 *  -> INCLUDES CREATION OF HIERARCHY AND SETTING OF ATTRIBUTE_TYPES FOR COLUMNS 
	 */
	public DefinitionServer setDefinitionARXForDataset(DatasetServer dataset, DefinitionServer definition){
		gen.setDefinitionARXForDataset(dataset, definition);
    	return definition;
	}
	
	public boolean existDefinitionServer(String datasetId, String definitionId){
		for(DefinitionServer ds: dp.deliverDatasetServer(datasetId).getParameters().getDefinitions()) {
			if(ds.getId().equals(definitionId)){
				return true;	
			}
		}
		return false;
	}
	
	public void removeDefinition(String datasetId, String definitionId){
		List<DefinitionClient> definitionClientList = dp.deliverDatasetClient(datasetId).getParameters().getDefinitions();
		List<DefinitionServer> definitionServerList = dp.deliverDatasetServer(datasetId).getParameters().getDefinitions();

		definitionClientList.remove(getDefinitionClient(datasetId, definitionId));
		definitionServerList.remove(getDefinition(datasetId, definitionId));
	}

	/**
	 * HIEARCHY OPERATIONS
	 */
	
	public void createHierarchy(String datasetId, String columnName,
								int min, int max, int biggerThan, String definitionId){
		
		DatasetServer dataset = dp.deliverDatasetServer(datasetId);
		DefinitionServer definition = gen.getServerDefinition(dataset, definitionId);
		
		gen.createHierarchy(dataset, columnName,
							min, max, biggerThan, definition);
	}
	
	public DefaultHierarchy getHierarchy(String datasetId, String definitionId, String columnId){
		DefinitionServer definition = getDefinition(datasetId, definitionId);

		for(ColumnServer c : definition.getColumns()){
			if(c.getId().equals(getColumn(definition, columnId))){
				return c.getHierarchy();
			}
		}
		return null;
	}
	
	/**
	 * || COLUMN OPERATIONS	
	 */

	public ColumnServer getColumn(DefinitionServer definition, String columnId){
		for(ColumnServer c : definition.getColumns()){
			if(c.getId().equals(columnId)){
				return c;
			}
		}
		return null;		
	}	
	
	public ColumnClient getColumnClient(String datasetId, String definitionId, String columnId){
		DefinitionClient definition = getDefinitionClient(datasetId, definitionId);
		for(ColumnClient c : definition.getColumns()){
			if(c.getId().equals(columnId)){
				return c;
			}
		}
		return null;		
	}
	
	/**
	 * || OTHERS
	 */
	
	private void addAnonymizedData(ARXResult result, DatasetServer dataset, ParameterClient param) {
		Transformer dt = new Transformer();
		
		ResultServer resultServer = new ResultServer(result, param.getIdDataset());
		ResultClient resultClient = new ResultClient(dt.returnResultIn2DArray(result), param.getIdDataset());
		
		ParameterResult parameterResult = am.createParameterResult(param);

		resultServer.setParameters(parameterResult);
		resultClient.setParameters(parameterResult);
		
		dataset.getResults().add(resultServer);
		dp.deliverDatasetClient(dataset.getProperties().getId()).getResults().add(resultClient);
	}

	public List<String> getAttributeTypesForDefinition(){
		return gen.getAttributeTypesForDefinition();
	}

	/**
	 * Getters and Setters
	 */
	
	public PeersManager getManager() {
		return manager;
	}

	public void setManager(PeersManager manager) {
		this.manager = manager;
	}

	public Generalizer getGen() {
		return gen;
	}

	public void setGen(Generalizer gen) {
		this.gen = gen;
	}

	public DatasetProvider getDp() {
		return dp;
	}

	public void setDp(DatasetProvider dp) {
		this.dp = dp;
	}

	public AlgorithmManager getAm() {
		return am;
	}

	public void setAm(AlgorithmManager am) {
		this.am = am;
	}

	public DataImporter getDi() {
		return di;
	}

	public void setDi(DataImporter di) {
		this.di = di;
	}

}
