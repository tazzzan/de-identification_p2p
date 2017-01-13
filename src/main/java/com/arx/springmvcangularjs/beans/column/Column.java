package com.arx.springmvcangularjs.beans.column;

import java.util.UUID;

/**
 * CAUTION: in 'datasetTransformed' the column does not contain 'DefaultHierarchy'
 *          it contains 'hierarchyTransformed' instead !!!
 * @author ilja
 *
 */

public class Column implements java.io.Serializable {
	
	private static final long serialVersionUID = -1125964321801437740L;
	
	private String name;
	private String id; 
	private String columnType;
	
	// Constructors
	
	public Column() {
		this.id = UUID.randomUUID().toString();
	}
	
	//Getters and Setters
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	};
}

