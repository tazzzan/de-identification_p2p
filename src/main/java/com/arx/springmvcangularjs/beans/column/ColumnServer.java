package com.arx.springmvcangularjs.beans.column;

import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;

public class ColumnServer implements java.io.Serializable {
	
	private static final long serialVersionUID = -1125964321801437740L;
	
	private String id;
	private String name; 
	private String attributeType;
	private int minGeneralization;
	private int maxGeneralization;
	private DefaultHierarchy hierarchy;

	public ColumnServer() {};
	
	public ColumnServer(String id){
		setId(id);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAttributeType() {
		return attributeType;
	}

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public int getMinGeneralization() {
		return minGeneralization;
	}

	public void setMinGeneralization(int minGeneralization) {
		this.minGeneralization = minGeneralization;
	}

	public int getMaxGeneralization() {
		return maxGeneralization;
	}

	public void setMaxGeneralization(int maxGeneralization) {
		this.maxGeneralization = maxGeneralization;
	}

	public DefaultHierarchy getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(DefaultHierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}
}
