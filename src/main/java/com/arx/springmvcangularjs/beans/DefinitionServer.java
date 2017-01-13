package com.arx.springmvcangularjs.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.arx.springmvcangularjs.beans.column.ColumnServer;

public class DefinitionServer implements java.io.Serializable {
	
	private static final long serialVersionUID = -1125964321801437740L;

	private String id; 
	private String datasetName;
	
	private List<ColumnServer> columns;
	
	public DefinitionServer(){
		this.id = UUID.randomUUID().toString();
		this.columns = new ArrayList<ColumnServer>();
	}
	
	public DefinitionServer(String id){
		this.id = id;
		this.columns = new ArrayList<ColumnServer>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<ColumnServer> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnServer> columns) {
		this.columns = columns;
	}
	
	
}
