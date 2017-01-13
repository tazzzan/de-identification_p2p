package com.arx.springmvcangularjs.beans.parameter;


import com.arx.springmvcangularjs.beans.DefinitionClient;

public class ParameterResult implements java.io.Serializable {
	
	private static final long serialVersionUID = -1125964321801437740L;
	
	private int kAnonymityValue;   
	private double kAnonymityMaxOutliers;
	 
	private DefinitionClient definition;
	
	private String informationLoss;
	
	public ParameterResult(){}

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


	public DefinitionClient getDefinition() {
		return definition;
	}

	public void setDefinition(DefinitionClient definition) {
		this.definition = definition;
	}

	public String getInformationLoss() {
		return informationLoss;
	}

	public void setInformationLoss(String informationLoss) {
		this.informationLoss = informationLoss;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	};
	
	
	
}
