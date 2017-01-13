package com.arx.springmvcangularjs.beans.dataset;

import com.arx.springmvcangularjs.beans.property.Properties;

/**
 * 
 * Maybe the most important class is 'Dataset'
 * In contrast to 'Data' from ARX this class extends:
 * 
 * - properties {id; path; name; rowNum; columnNum; columns<Column...>} 
 * 
 * and further attributes [parameters, result]
 * in DatasetClient, DatasetServer and DatasetP2P
 * 
 * @author ilja
 *
 */

public abstract class Dataset {
	
	private Properties properties;
	
	public Dataset(){
		this.properties = new Properties();
	}


	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
	