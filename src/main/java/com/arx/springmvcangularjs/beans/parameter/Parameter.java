package com.arx.springmvcangularjs.beans.parameter;


public class Parameter implements java.io.Serializable {
	
	private static final long serialVersionUID = -1125964321801437740L;
	
	private String idDataset;
	private String settedDefinitionId;
		
	public Parameter () {}

	public String getIdDataset() {
		return idDataset;
	}

	public void setIdDataset(String idDataset) {
		this.idDataset = idDataset;
	}

	public String getSettedDefinitionId() {
		return settedDefinitionId;
	}

	public void setSettedDefinitionId(String settedDefinitionId) {
		this.settedDefinitionId = settedDefinitionId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
