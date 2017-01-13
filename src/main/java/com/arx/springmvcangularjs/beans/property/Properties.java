package com.arx.springmvcangularjs.beans.property;

import java.util.List;

import com.arx.springmvcangularjs.beans.column.Column;

public class Properties implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;

	private String id;
	private String path;
	private String name;
	private int rowNum;
	private int columnNum;
	private String peerId;
	
	private List<Column> columns; 
	
	
	public Properties(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String getPeerId() {
		return peerId;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}


	public int getRowNum() {
		return rowNum;
	}

	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}

	public int getColumnNum() {
		return columnNum;
	}

	public void setColumnNum(int columnNum) {
		this.columnNum = columnNum;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
