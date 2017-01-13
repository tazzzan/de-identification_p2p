package com.arx.springmvcangularjs.beans;

import java.util.List;

import com.arx.springmvcangularjs.beans.column.ColumnClient;

public class DefinitionClient implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;
	private String id; 
	
	private List<ColumnClient> columns;
	
	public DefinitionClient() {}
	
	public DefinitionClient(String id){
		this.id = id;
	}
	public DefinitionClient(String id, List<ColumnClient> columns){
		this.id = id;
		this.columns = columns;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ColumnClient> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnClient> columns) {
		this.columns = columns;
	}
}
