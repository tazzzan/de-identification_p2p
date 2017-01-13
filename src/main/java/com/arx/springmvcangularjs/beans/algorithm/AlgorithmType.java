package com.arx.springmvcangularjs.beans.algorithm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.deidentifier.arx.criteria.EntropyLDiversity;
import org.deidentifier.arx.criteria.EqualDistanceTCloseness;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;
import org.deidentifier.arx.criteria.RecursiveCLDiversity;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.column.ColumnClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;

/**
 * Class 'AlgorithmType' is essential if it comes to creation of new algorithms
 * Distinct types of algorithms require different methods to build configurations
 * This class knows how to set up configurations for each type of algorithm
 * 
 * Supported algorithm types:
 * K_ANONYMITY     
 * T_CLOSENESS      
 * L_DIVERSITY
 * D_PRESENCE 
 * D_DISCLOSURE
 * 
 * AlgorithmType."..." invokes a constuctor of 'AlgorithmType' with selection of specific type
 * 
 * createConfig(params)
 * toString()
 * 
 * @author Ilja Lichtenberg
 *
 */

public class AlgorithmType implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;

	private static int  ALGR_TYPE_KA                = 0;
	private static int  ALGR_TYPE_TC                = 1;
	private static int  ALGR_TYPE_LD                = 2;
	private static int  ALGR_TYPE_DP                = 3;
	private static int  ALGR_TYPE_DD                = 4;
	
	private static AlgorithmType K_ANONYMITY      = new AlgorithmType(ALGR_TYPE_KA);
	private static AlgorithmType T_CLOSENESS      = new AlgorithmType(ALGR_TYPE_TC);
	private static AlgorithmType L_DIVERSITY      = new AlgorithmType(ALGR_TYPE_LD);
	private static AlgorithmType D_PRESENCE       = new AlgorithmType(ALGR_TYPE_DP);
	private static AlgorithmType D_DISCLOSURE     = new AlgorithmType(ALGR_TYPE_DD);
	
	private int type;
	
	public AlgorithmType() {}
	
	public AlgorithmType(int type) {
		this.type = type;	
	}
	
	
	public ARXConfiguration createConfig(ParameterClient parameters){
		ARXConfiguration config = ARXConfiguration.create();

		if(type==ALGR_TYPE_KA){	 /** k-anonymity */
			
				/** Get Parameters for Criteria */			
				int kAnonymity = parameters.getkAnonymityValue();
				double maxOutliers = parameters.getkAnonymityMaxOutliers();
				
				/** Add criteria */			
				config.addCriterion(new KAnonymity(kAnonymity));
				config.setMaxOutliers(maxOutliers);
		}
		
		if(type==ALGR_TYPE_TC){	/** t-closeness */
				ColumnClient columnToAnonymize = null;
				
				for(DefinitionClient dc : parameters.getDefinitions()){
					if(dc.getId().equals(parameters.getSettedDefinitionId())){
						for(ColumnClient cc : dc.getColumns()){
							if(cc.getAngularParameters().isAlgorithmIsTCloseness()){
								columnToAnonymize = cc; 
							}
						}
					}
				}
				/** Get Parameters for Criteria */
				String columnNameSensitive = 		columnToAnonymize.getName();
				String path =						columnToAnonymize.getAngularParameters().getAlgorithmTClosenessPath();
				double hierarchicalGroundDistance = columnToAnonymize.getAngularParameters().getAlgorithmTClosenessHierarchialGroundDistance();
				
				/** Get Hierarchy for Criteria */
				Hierarchy hierarchy = null;
				
				if(!path.equals("")){
					try {
						 hierarchy = Hierarchy.create(new File(path),
														       StandardCharsets.UTF_8);
					} 
					catch (IOException e) {e.printStackTrace();}
				}

				
				/** Add criteria */			
				if(hierarchy!=null){
					config.addCriterion(new HierarchicalDistanceTCloseness(columnNameSensitive,
																		   hierarchicalGroundDistance,
																		   hierarchy));
				}
				
				if(hierarchy==null){
					config.addCriterion(new EqualDistanceTCloseness(columnNameSensitive, 
																	hierarchicalGroundDistance));	
				}	
		}
		
		if(type==ALGR_TYPE_LD){	/** l-diversity */
			
			List<ColumnClient> columnsToAnonymize = new ArrayList<ColumnClient>();
			
			for(DefinitionClient dc : parameters.getDefinitions()){
				if(dc.getId().equals(parameters.getSettedDefinitionId())){
					for(ColumnClient cc : dc.getColumns()){
						if(cc.getAngularParameters().getAlgorithmIsLDiversity()){
							columnsToAnonymize.add(cc); 
						}
					}
				}
			}
			
			for(ColumnClient columnToAnonymize : columnsToAnonymize){
				/** Get Parameters for Criteria */	
				String columnNameSensitive = 		columnToAnonymize.getName();
				int lDiversity = 				    columnToAnonymize.getAngularParameters().getAlgorithmLDiversityValue();
				boolean isEntropyLDiversity = 		false;
				int recursiveC =					columnToAnonymize.getAngularParameters().getAlgorithmLDiversityRecursiveC();
				
				/** Add criteria */			
				if(isEntropyLDiversity!=true){
					config.addCriterion(new DistinctLDiversity(columnNameSensitive, lDiversity));
				}
				if(isEntropyLDiversity==true){
					config.addCriterion(new EntropyLDiversity(columnNameSensitive, lDiversity));
				}
				if(recursiveC!=0){
					config.addCriterion(new RecursiveCLDiversity(columnNameSensitive, recursiveC, lDiversity));
				}
			}
			
		}
		
		return  config;
	}
	
    
    public String toString() {
        switch (type) {
        case 0:
            return "K_ANONYMITY";
        case 1:
            return "T_CLOSENESS";
        case 2:
            return "L_DIVERSITY";
        case 3:
            return "D_PRESENCE";
        case 4:
        	return "DELTA_DISCLOSURE";
        default:
            return "UNKNOWN_ALGORITHM_TYPE";
        }
    }

	public int getALGR_TYPE_KA() {
		return ALGR_TYPE_KA;
	}

	public void setALGR_TYPE_KA(int aLGR_TYPE_KA) {
		ALGR_TYPE_KA = aLGR_TYPE_KA;
	}

	public int getALGR_TYPE_TC() {
		return ALGR_TYPE_TC;
	}

	public void setALGR_TYPE_TC(int aLGR_TYPE_TC) {
		ALGR_TYPE_TC = aLGR_TYPE_TC;
	}

	public int getALGR_TYPE_LD() {
		return ALGR_TYPE_LD;
	}

	public void setALGR_TYPE_LD(int aLGR_TYPE_LD) {
		ALGR_TYPE_LD = aLGR_TYPE_LD;
	}

	public int getALGR_TYPE_DP() {
		return ALGR_TYPE_DP;
	}

	public void setALGR_TYPE_DP(int aLGR_TYPE_DP) {
		ALGR_TYPE_DP = aLGR_TYPE_DP;
	}

	public int getALGR_TYPE_DD() {
		return ALGR_TYPE_DD;
	}

	public void setALGR_TYPE_DD(int aLGR_TYPE_DD) {
		ALGR_TYPE_DD = aLGR_TYPE_DD;
	}

	public static AlgorithmType getK_ANONYMITY() {
		return K_ANONYMITY;
	}

	public static void setK_ANONYMITY(AlgorithmType k_ANONYMITY) {
		K_ANONYMITY = k_ANONYMITY;
	}

	public static AlgorithmType getT_CLOSENESS() {
		return T_CLOSENESS;
	}

	public static void setT_CLOSENESS(AlgorithmType t_CLOSENESS) {
		T_CLOSENESS = t_CLOSENESS;
	}

	public static AlgorithmType getL_DIVERSITY() {
		return L_DIVERSITY;
	}

	public static void setL_DIVERSITY(AlgorithmType l_DIVERSITY) {
		L_DIVERSITY = l_DIVERSITY;
	}

	public static AlgorithmType getD_PRESENCE() {
		return D_PRESENCE;
	}

	public static void setD_PRESENCE(AlgorithmType d_PRESENCE) {
		D_PRESENCE = d_PRESENCE;
	}

	public static AlgorithmType getD_DISCLOSURE() {
		return D_DISCLOSURE;
	}

	public static void setD_DISCLOSURE(AlgorithmType d_DISCLOSURE) {
		D_DISCLOSURE = d_DISCLOSURE;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}

