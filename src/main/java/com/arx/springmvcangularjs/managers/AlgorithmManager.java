package com.arx.springmvcangularjs.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.criteria.PrivacyCriterion;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmServer;
import com.arx.springmvcangularjs.beans.algorithm.AlgorithmType;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterResult;
import com.arx.springmvcangularjs.handler.AlgorithmProvider;
import com.arx.springmvcangularjs.handler.DatasetUpdater;
import com.arx.springmvcangularjs.handler.Transformer;

/**
 * 'AlgorithmManager' is required to create/specify the configuration for anonymization
 * and finally to create and store new 'Algorithm' in 'AlgorithmProvider.algorithms' and in 'Dataset'
 * 
 * AlgorithmProvider ap
 * 
 * || ALGORITHM OPERATIONS
 * createAnonymizationAlgorithm()
 * addNewAlgorithm
 * addConfigurationToAlgorithm()
 * updateAlgorithm
 * setAlgorithmGeneral
 * updateAlgorithmGeneral
 * getAlgorithmFromDS
 * setAlgorithm 
 * 
 * || PRIVACY CRITERIA OPERATIONS:
 * getSavedAlgorithmCriteria * 
 * containsPC()
 * containsKA()
 * isKA()
 * getKA()
 * containsDL()
 * isDL()
 * getDL()
 * 
 * @author ilja
 *
 */

public class AlgorithmManager {
	
	AlgorithmProvider ap;	
	DatasetUpdater update;
	
	/**
	 * Constructors
	 */
		
	public AlgorithmManager () {};
	
	public AlgorithmManager (AlgorithmProvider algorithmProvider, DatasetManager dm){
		this.ap = algorithmProvider;
		this.update = new DatasetUpdater(dm.getDp(), dm);
	}
	
		
	/**
	 * METHODS
	 */
	
	/** 
	 * || ALGORITHM OPERATIONS
	 * createAnonymizationAlgorithm()
	 * addNewAlgorithm
	 * addConfigurationToAlgorithm()
	 * updateAlgorithm
	 * setAlgorithmGeneral
	 * updateAlgorithmGeneral
	 * getAlgorithmFromDS
	 * setAlgorithm 
	 */
	
	/** Creates a new algorithm for anonymization
	 * -> sets the new algorithm automatically as algorithm_general
	 */	
	public void createAnonymizationAlgorithm(DatasetServer dataset, AlgorithmType algorithmType, ParameterClient parameters) {

		AlgorithmServer algorithm = new AlgorithmServer(parameters.getSettedDefinitionId());
		
		ARXConfiguration config = algorithmType.createConfig(parameters);
		
		addConfigurationToAlgorithm(algorithm, config, algorithmType);
		
		addNewAlgorithm(dataset, algorithm);
		setAlgorithm(algorithm.getId(), dataset);	
	}
	
	public void addNewAlgorithm(DatasetServer dataset, AlgorithmServer algorithm) {		
//		ap.addAlgorithm(algorithm);	
		dataset.getParameters().getAlgorithms().add(algorithm);
	}
	
	private AlgorithmServer addConfigurationToAlgorithm(AlgorithmServer algorithm, ARXConfiguration config, AlgorithmType at){
		Transformer dt = new Transformer();
		
		algorithm.setConfig(config);
		algorithm.setAlgorithmType(at);
		algorithm.setCriteria(dt.returnCriteriaInStringList(config));
		return algorithm;
	}	
	
	public AlgorithmServer getAlgorithmFromDS(DatasetServer dataset, String algorithmId){
		AlgorithmServer algorithm = null;
		
		for(AlgorithmServer as: dataset.getParameters().getAlgorithms()){
			if(as.getId().equals(algorithmId)){
				algorithm = as;
			}
		}
		return algorithm;
	}
	
	/**ONLY POSSIBLE IF AN ALGORITHM WAS CREATED BEFORE	
	 */
	public void setAlgorithm(String algorithmId, DatasetServer dataset){;
		AlgorithmServer algorithm = getAlgorithmFromDS(dataset, algorithmId);
		
		updateAlgorithmGeneral(dataset, algorithm);
	}
	
	public void updateAlgorithm(DatasetServer dataset, String algorithmId, ParameterClient parameters){
		AlgorithmServer algorithm = getAlgorithmFromDS(dataset, algorithmId);
		ARXConfiguration config = algorithm.getAlgorithmType().createConfig(parameters);
		
		addConfigurationToAlgorithm(algorithm, config, algorithm.getAlgorithmType());
		setAlgorithm(algorithm.getId(), dataset);
	}

	public void setAlgorithmGeneral(DatasetServer dataset, AlgorithmServer algorithmGeneral){
		dataset.getParameters().setAlgorithmGeneral(algorithmGeneral);
	}
	
	public void updateAlgorithmGeneral(DatasetServer dataset, AlgorithmServer algorithm){
		AlgorithmServer algorithmGeneral = null;
		
		if(dataset.getParameters().getAlgorithmGeneral() != null){
			algorithmGeneral = dataset.getParameters().getAlgorithmGeneral();
		}
		if(dataset.getParameters().getAlgorithmGeneral() == null){
			algorithmGeneral = new AlgorithmServer(dataset.getParameters().getSettedDefinitionId());
		}		
		
		/** Check if algorithm criteria already exists and needs to be updated 
		 */
		for(PrivacyCriterion pc : algorithm.getConfig().getCriteria()){
			if(dataset.getParameters().getAlgorithmGeneral() != null){
				if(isKA(pc.toString())){
					if(containsKA(algorithmGeneral)==true){
						algorithmGeneral.getConfig().removeCriterion(getKA(algorithmGeneral));
						algorithmGeneral.getConfig().addCriterion(pc);
					}
				}
				if(isDL(pc.toString())){
					if(containsDL(algorithmGeneral, pc)==true){
							
							algorithmGeneral.getConfig().removeCriterion(getDL(algorithmGeneral));
							algorithmGeneral.getConfig().addCriterion(pc);
					}
				}

				if(containsPC(algorithmGeneral, pc)==false){ 
					algorithmGeneral.getConfig().addCriterion(pc);// if criteria already exists than it will be skipped
				}
			}
			else{
				algorithmGeneral.getConfig().addCriterion(pc);
			}
		}
		
		if(algorithm.getConfig().getMaxOutliers()!=0){
			algorithmGeneral.getConfig().setMaxOutliers(algorithm.getConfig().getMaxOutliers());
		}
		if(algorithm.getConfig().getMetric()==null){
			algorithmGeneral.getConfig().setMetric(algorithm.getConfig().getMetric());
		}
		
		dataset.getParameters().setAlgorithmGeneral(algorithmGeneral);				
		

		getSavedAlgorithmCriteria(dataset);
	}
	
	/**
	 * || PRIVACY CRITERIA OPERATIONS:
	 * getSavedAlgorithmCriteria * 
	 * containsPC()
	 * containsKA()
	 * isKA()
	 * getKA()
	 * containsDL()
	 * isDL()
	 * getDL()
	 */
	
	public ParameterResult createParameterResult(ParameterClient param){
		ParameterResult parameterResult = new ParameterResult();
		parameterResult.setkAnonymityValue(param.getkAnonymityValue());
		parameterResult.setkAnonymityMaxOutliers(param.getkAnonymityMaxOutliers());
		
		for(DefinitionClient definition : param.getDefinitions()){
			if(definition.getId().equals(param.getSettedDefinitionId())){
				parameterResult.setDefinition(definition);
			}
		}
		return parameterResult;
	}
	
	public List<String> getSavedAlgorithmCriteria(DatasetServer dataset){
		
		List<String> pcList = new ArrayList<String>();
		for(PrivacyCriterion pc : dataset.getParameters().getAlgorithmGeneral().getConfig().getCriteria()){
			pcList.add(pc.toString());
			pcList.add(String.valueOf(dataset.getParameters().getAlgorithmGeneral().getConfig().getMaxOutliers()));
			pcList.add(String.valueOf(dataset.getParameters().getAlgorithmGeneral().getConfig().getMetric().getSuppressionFactor()));
		}
		
		return pcList;
	}
	
	/** check for any criterion in Algorithm
	 */	
	public boolean containsPC(AlgorithmServer algorithm, PrivacyCriterion pc){
		boolean isTrue = false;
		Set<PrivacyCriterion> setPC = algorithm.getConfig().getCriteria();
		for (PrivacyCriterion pcInAlgorithm : setPC){
			if (pcInAlgorithm.toString().equals(pc.toString())){
				isTrue = true;
			}
		}
		return isTrue;
	}
	
	/** check for k-anonymity criterion in Algorithm
	 */	
	public boolean containsKA(AlgorithmServer algorithm){
		boolean isTrue = false;
		Set<PrivacyCriterion> setPC = algorithm.getConfig().getCriteria();
		for (PrivacyCriterion pcInAlgorithm : setPC){
			if(pcInAlgorithm.toString().contains("anonymity")){
				isTrue = true;
		      }
		}
		return isTrue;
	}
	
	public boolean isKA(String criterion){
		boolean isTrue = false;
		
		if(criterion.toString().contains("anonymity")){
			isTrue = true;
	      }
		return isTrue;
	}
	
	public PrivacyCriterion getKA(AlgorithmServer algorithm){
		for (PrivacyCriterion pc : algorithm.getConfig().getCriteria()){
			if(isKA(pc.toString())){
				return pc;
			}
		}
		return null;
	}
	
	/** check for l-diversity criterion in Algorithm
	 */	
	public boolean containsDL(AlgorithmServer algorithm, PrivacyCriterion pcReceived){
		boolean isTrue = false;
		Set<PrivacyCriterion> setPC = algorithm.getConfig().getCriteria();
		List<PrivacyCriterion> toCheckIfDistinct = new ArrayList<PrivacyCriterion>();

		for (PrivacyCriterion pcInAlgorithm : setPC){
			if(pcInAlgorithm.toString().contains("diversity")){
				toCheckIfDistinct.add(pcInAlgorithm);
				isTrue = true;
		      }
		}
		
		for (PrivacyCriterion pc : toCheckIfDistinct){
			String[] s1 = pc.toString().split(" +");
			String[] s2 = pcReceived.toString().split(" +");
			if(s1[s1.length-1].equals(s2[s2.length-1])){
				 isTrue = true;
			 }
			else{
				isTrue = false;
			}
		}
		
		return isTrue;
	}
	
	public boolean isDL(String criterion){
		boolean isTrue = false;
		if(criterion.toString().contains("diversity")){
			isTrue = true;
	      }
		return isTrue;
	}
	

	public PrivacyCriterion getDL(AlgorithmServer algorithm){
		for (PrivacyCriterion pc : algorithm.getConfig().getCriteria()){
			if(isDL(pc.toString())){
				return pc;
			}
		}
		return null;
	}
	
	/** check for l-diversity-recursive criterion in Algorithm
	 */	
	public boolean containsDLR(AlgorithmServer algorithm, PrivacyCriterion pcReceived){
		boolean isTrue = false;
		Set<PrivacyCriterion> setPC = algorithm.getConfig().getCriteria();
		List<PrivacyCriterion> toCheckIfDistinct = new ArrayList<PrivacyCriterion>();

		for (PrivacyCriterion pcInAlgorithm : setPC){
			if(pcInAlgorithm.toString().contains("recursive")){
				toCheckIfDistinct.add(pcInAlgorithm);
				isTrue = true;
		      }
		}
		
		for (PrivacyCriterion pc : toCheckIfDistinct){
			String[] s1 = pc.toString().split(" +");
			String[] s2 = pcReceived.toString().split(" +");

			if(s1[s1.length-1].equals(s2[s2.length-1])){
				isTrue = true;
			}
			else{
				isTrue = false;
			}
		}
		
		return isTrue;
	}
	
	public boolean isDLR(String criterion){
		boolean isTrue = false;
		if(criterion.toString().contains("recursive")){
			isTrue = true;
	      }
		return isTrue;
	}

	public PrivacyCriterion getDLR(AlgorithmServer algorithm){
		for (PrivacyCriterion pc : algorithm.getConfig().getCriteria()){
			if(isDLR(pc.toString())){
				return pc;
			}
		}
		return null;
	}
	
	/**
	 * Getters and Setters
	 */

	public AlgorithmProvider getAp() {
		return ap;
	}

	public void setAp(AlgorithmProvider ap) {
		this.ap = ap;
	}

	public DatasetUpdater getUpdate() {
		return update;
	}

	public void setUpdate(DatasetUpdater update) {
		this.update = update;
	}
}
