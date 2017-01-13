package com.arx.springmvcangularjs.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.criteria.PrivacyCriterion;

import com.arx.springmvcangularjs.beans.column.Column;
import com.arx.springmvcangularjs.beans.column.ColumnClient;
import com.arx.springmvcangularjs.beans.column.ColumnServer;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;

import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataHandle;

/**
 * 'DatasetTransformer' transforms 'Dataset' partials  
	 * NEW: 
	 * 
	 * Columns -> List<String> hierarchyTransformed
	 * Data -> Object[][] data
	 * Result -> Object[][] result
	 * 
	 * OLD:
	 *  
	 * Name
	 * ID
	 * Path
	 * RowNum
	 * ColumnNum
 * 
 * returnDataInArray(data) 
 * returnResultInArray(result) 
 * returnTransformedHierarchy(hierarchy)
 * ...
 * 
 * @author ilja
 *
 */

public class Transformer {
		
	
	// Constructors
	
	public Transformer() {}; 
	
	// Functions
	
	public Object[][] returnDataIn2DArray(Data data) {	

		DataHandle handle = data.getHandle();

		try {
			handle.save("datasets/handle.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int numRows = handle.getNumRows();
		int numColumns = handle.getNumColumns();
		
		// NO CHANCE TO LOAD 1000 ROWS INTO BROWSER 
		if (numRows>40){
			numRows = 40;
		}
		
		Object[][] table = new Object[numRows][numColumns];
		
		for (int i=0; i<numRows; i++) {			
			for (int ii=0; ii<numColumns; ii++) {
				String value = handle.getValue(i, ii);
				table[i][ii] = value;	
			}
		}
		handle.release();
		return table;
	}
	
	// doesnt matter if true or false 
	// needs to be implemented for internal usage of transformed datasets
	// NEEDS TO BE AVOIDED !!!!!!!!!!!	
	public Object[][] returnDataIn2DArray(Data data, boolean notForBrowser) {	

		DataHandle handle = data.getHandle();

		int numRows = handle.getNumRows();
		int numColumns = handle.getNumColumns();
		
		Object[][] table = new Object[numRows][numColumns];
		
		for (int i=0; i<numRows; i++) {			
			for (int ii=0; ii<numColumns; ii++) {
				String value = handle.getValue(i, ii);
				table[i][ii] = value;	
			}
		}
		handle.release();
		return table;
	}
	

	
	public Object[][] returnResultIn2DArray(ARXResult result) {	
		
		DataHandle handle = result.getOutput();

		
		int numRows = handle.getNumRows();
		int numColumns = handle.getNumColumns();
		
		// NO CHANCE TO LOAD 1000 ROWS INTO BROWSER 
		if (numRows>40){
			numRows = 40;
		}
		
		Object[][] table = new Object[numRows][numColumns];
		
		for (int i=0; i<numRows; i++) {			
			for (int ii=0; ii<numColumns; ii++) {
				String value = handle.getValue(i, ii);
				table[i][ii] = value;	
			}
		}
		handle.release();
		return table;
	}
	
	/**
	 * Creates an example (!) hierarchy OUT OF 'DatasetServer' (!)
	 * --> only first row is returned
	 * --> though if too many different characters the user will see just one example
	 * 
	 * @param dataset
	 * @param column
	 * @return
	 */
	public List<String> returnTransformedSettedHierarchy(DatasetServer dataset, ColumnServer column){
		List<String> hierarchy = new ArrayList<String>();
		String[][] qia = dataset.getData().getDefinition().getHierarchy(column.getName());

		for (String[] qi1: qia){
			for(int i=0; i<qi1.length; i++){
				hierarchy.add(qi1[i]);
			}
			break;
		}
		return hierarchy;
	}
	
	/**
	 * Creates an example (!) hierarchy OUT OF 'DatasetServer' (!)
	 * --> only first row is returned
	 * --> though if too many different characters the user will see just one example
	 * 
	 * @param dataset
	 * @param column
	 * @return
	 */
	public List<String> returnTransformedHierarchy(ColumnServer column){
		List<String> hierarchy = new ArrayList<String>();
		String[][] qia = column.getHierarchy().getHierarchy();

		for (String[] qi1: qia){
			for(int i=0; i<qi1.length; i++){
				hierarchy.add(qi1[i]);
				System.out.println("line of hierarchy_transformed: "+ qi1[i]);
			}
			break;
		}
		return hierarchy;
	}

	public List<ColumnClient> returnColumnClientList(List<Column> columns){
		List<ColumnClient> columnClientList = new ArrayList<ColumnClient>();
		
		for(Column columnExisting: columns){				
			ColumnClient column = new ColumnClient(columnExisting.getId());
			column.setName(columnExisting.getName());
			columnClientList.add(column);
		}		
		
		return columnClientList;
	}
	public List<String> returnCriteriaInStringList(ARXConfiguration config){
		List<String> pcList = new ArrayList<String>();
		for(PrivacyCriterion pc : config.getCriteria()){
			pcList.add(pc.toString());
			pcList.add(String.valueOf(config.getMaxOutliers()));
			pcList.add(String.valueOf(config.getMetric().getSuppressionFactor()));
			
		}
		return pcList;
	}
	
	
	
}
