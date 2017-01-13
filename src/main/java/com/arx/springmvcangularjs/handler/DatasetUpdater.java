package com.arx.springmvcangularjs.handler;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.DefinitionServer;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmClient;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmServer;
import com.arx.springmvcangularjs.beans.column.ColumnClient;
import com.arx.springmvcangularjs.beans.column.ColumnServer;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterServer;
import com.arx.springmvcangularjs.beans.property.Properties;
import com.arx.springmvcangularjs.managers.DatasetManager;

/**
 * This class is needed to update DatasetServer DatasetClient and DatasetP2P simultanously
 * 
 * - updateData
 * - updateProp
 * - updateParam
 * 
 * @author ilja
 *
 */

public class DatasetUpdater {
	
	DatasetProvider dp; 
	DatasetManager dm;
	
	Transformer dt = new Transformer();
	
	
	public DatasetUpdater(DatasetProvider dp, DatasetManager dm){
		this.dp = dp;
		this.dm = dm;
	}
	
	
	/*
	 * For  Client -> Server <<only>>
	 *
	 * 
	 */
	public void updateProp(Properties prop){
		
		DatasetClient datasetClient = dp.deliverDatasetClient(prop.getId());
		DatasetServer datasetServer = dp.deliverDatasetServer(prop.getId());
		
		// no need to go a specific way
		datasetClient.setProperties(prop);
		datasetServer.setProperties(prop);
	}
	

	public void updateParam(ParameterServer param){
		/**
		 * PARAMETER		 
		 */
		DatasetClient datasetClient= dp.deliverDatasetClient(param.getIdDataset());
		DatasetServer datasetServer = dp.deliverDatasetServer(param.getIdDataset());
		
		if(datasetServer!=null){
			datasetServer.setParameters(param);
		}
		if(datasetClient!=null){
			/** Create parameter_client to update
			 */
			ParameterClient parametersClientLocal = datasetClient.getParameters();	
			
			/** Create algorihm_list_client to update
			 */
			List<AlgorithmClient> algorithmListNew = new ArrayList<AlgorithmClient>();
			
			for(AlgorithmServer as: param.getAlgorithms()){
				AlgorithmClient algorithmNew = new AlgorithmClient(as.getId(), param.getSettedDefinitionId());
				algorithmNew.setCriteria(as.getCriteria());
				algorithmNew.setAlgorithmType(as.getAlgorithmType().toString());
				
				algorithmListNew.add(algorithmNew);
			}
			
			parametersClientLocal.setAlgorithms(algorithmListNew);
			parametersClientLocal.setSettedDefinitionId(param.getSettedDefinitionId());
			parametersClientLocal.setIdDataset(param.getIdDataset());
			
			/** Create definition_list_client to update
			 */
			List<DefinitionClient> definitionsListNew = new ArrayList<DefinitionClient>();
					
			for(DefinitionServer definitionServer: param.getDefinitions()){
				
				/** Create definition_client_new based on definition_server and definition_client_old
				 */
				DefinitionClient definitionOld = dm.getDefinitionClient(param.getIdDataset(), definitionServer.getId());
				DefinitionClient definitionNew = new DefinitionClient(definitionServer.getId(), definitionOld.getColumns());
										
				for(ColumnServer cs: definitionServer.getColumns()){
					for(ColumnClient cc: definitionNew.getColumns()){
						if(cc.getId().equals(cs.getId())){
							cc.setName(cs.getName());
							cc.setAttributeType(cs.getAttributeType());		
							if(cs.getHierarchy()!=null){
								cc.setHierarchy(dt.returnTransformedHierarchy(cs));
							}
						}
					}				
				}				
				definitionsListNew.add(definitionNew);
			}
			parametersClientLocal.setDefinitions(definitionsListNew); 
			
			datasetClient.setParameters(parametersClientLocal);
		}
	}
}
