package com.arx.springmvcangularjs.beans.column;

import java.util.ArrayList;
import java.util.List;

import com.arx.springmvcangularjs.beans.AngularParameters;

public class ColumnClient implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;

	private String id;
	private String name;
	private String attributeType = "bam";
	
	private AngularParameters angularParameters;
	private List<String> hierarchy;
		
	public ColumnClient() {}
	
	public ColumnClient (String id) {
		setId(id);
		angularParameters = new AngularParameters();
		hierarchy = new ArrayList<String>();
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

	public AngularParameters getAngularParameters() {
		return angularParameters;
	}

	public void setAngularParameters(AngularParameters angularParameters) {
		this.angularParameters = angularParameters;
	}

	public List<String> getHierarchy() {
		return hierarchy;
	}

	public void setHierarchy(List<String> hierarchy) {
		this.hierarchy = hierarchy;
	}

}
