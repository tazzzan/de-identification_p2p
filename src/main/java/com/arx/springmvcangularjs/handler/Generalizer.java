package com.arx.springmvcangularjs.handler;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.DataDefinition;
import org.deidentifier.arx.DataSubset;
import org.deidentifier.arx.DataType;
import org.deidentifier.arx.aggregates.AggregateFunction.AggregateFunctionBuilder;
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness;
import org.deidentifier.arx.criteria.KAnonymity;

import com.arx.springmvcangularjs.beans.DefinitionServer;
import com.arx.springmvcangularjs.beans.column.Column;
import com.arx.springmvcangularjs.beans.column.ColumnServer;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;

import org.deidentifier.arx.AttributeType.Hierarchy;
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy;
import org.deidentifier.arx.AttributeType.MicroAggregationFunction;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.Data.DefaultData;

public class Generalizer {

	/**
	 * set the definition for data set 
	 * 
	 * FROM parameters.getDefinition().definition
	 * TO   data.getDefinition()
	 *  
	 * ---> set 'column.name' and 'attribute type' 
	 * ---> 'attribute type' is a hierarchy in case of Quasi_Identifier
	 *
	 * @param dataset
	 */
	// ONLY POSSIBLE IF DEFINITION_SERVER WAS CREATED BEFORE
	// -> INCLUDES CREATION OF HIERARCHY AND SETTING OF ATTRIBUTE_TYPES FOR COLUMNS !!!
	public void setDefinitionARXForDataset(DatasetServer dataset, DefinitionServer definition){
		
		DataDefinition definitionARX = dataset.getData().getDefinition();
		DefinitionServer definitionDS = definition;
		
		dataset.getParameters().setSettedDefinitionId(definitionDS.getId());
      
        for (int i=0; i<definitionDS.getColumns().size(); i++){
        	AttributeType at = null;
        	
        	if(definitionDS.getColumns().get(i).getAttributeType().equals("identifying")){
        		at = AttributeType.IDENTIFYING_ATTRIBUTE;
           	}
        	if(definitionDS.getColumns().get(i).getAttributeType().equals("quasi_identifying")){
        		at = definitionDS.getColumns().get(i).getHierarchy();
        	}										
        	if(definitionDS.getColumns().get(i).getAttributeType().equals("insensitive")){
        		at = AttributeType.INSENSITIVE_ATTRIBUTE;
        	}
        	if(definitionDS.getColumns().get(i).getAttributeType().equals("sensitive")){
        		at = AttributeType.SENSITIVE_ATTRIBUTE;
        	}
        	if(definitionDS.getColumns().get(i).getAttributeType().equals("unknown")){
        		at = AttributeType.INSENSITIVE_ATTRIBUTE;
        	}
        	definitionARX.setAttributeType(definitionDS.getColumns().get(i).getName(), at);
        }
	}	
	
	/**
	 * Here the application creates a hierarchy.normal != interval.based
	 * 
	 * Input [dataset | column.name | min | max | biggerThan]
	 * 
	 * Output [hierarchy.default]
	 * 
	 * 
	 * If min (or) max exist than the generalization will set Minimum/Maximum-Generalization
	 * 
	 * If biggerThan not setted than the median of list is taken
	 * 
	 */
	public void createHierarchy(DatasetServer dataset,
			String columnName,
			int min,
			int max,
			int biggerThan,
			DefinitionServer definitionDS){

		Transformer dt = new Transformer(); 
		
		DefaultHierarchy hierarchy = Hierarchy.create();
		Column columnToGeneralize = new Column();
		String attributeForGeneralization = "";
		int indexOfColumnToGeneralize = 0x0;

		/**
		 *  Select the column to generalize
		 *  Get the attribute for Generalization 
		 *  Get the index of column to generalize
		 */

		for(int i=0; i<dataset.getProperties().getColumns().size(); i++){
			columnToGeneralize = dataset.getProperties().getColumns().get(i);
			
			if(columnToGeneralize.getName().equals(columnName)){
				attributeForGeneralization = columnToGeneralize.getColumnType();
				indexOfColumnToGeneralize = i;
				break;
			}	
		}

		/**
		*  Set the min and max for Generalization
		*/
		DataDefinition definitionARX = dataset.getData().getDefinition();
		
		if(min!=0){
			definitionARX.setMinimumGeneralization(columnName, min);
		}
		if(max!=0){
			definitionARX.setMaximumGeneralization(columnName, max);
			
			// for definitionDS
			for(ColumnServer cs: definitionDS.getColumns()){
				if(cs.getName().equals(columnName)){
					cs.setMinGeneralization(min);
					cs.setMaxGeneralization(max);
				}
			}
		}
		
		/**
		* End
		*/


		switch(attributeForGeneralization){
		case "Integer":{
			List<String> integers = new ArrayList<String>();
			List<Integer> integersInInt = new ArrayList<Integer>();
			
			for (int i=0; i<dataset.getProperties().getRowNum(); i++) {
				Object[][] dataObject = dt.returnDataIn2DArray(dataset.getData(), true);
				integers.add(dataObject[i][indexOfColumnToGeneralize].toString());
				integersInInt.add(Integer.parseInt(dataObject[i][indexOfColumnToGeneralize].toString()));
			}
			

			if(biggerThan==0){
			biggerThan=median(integersInInt);
			}

			for(int i=0; i<integers.size(); i++) {
				String[] parts = integers.get(i).split("");
	
				if (parts.length==1){
					hierarchy.add(integers.get(i),
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "*"
					);	
				}
	
				if (parts.length==2){			
					hierarchy.add(integers.get(i),
								  generalizeIntegerByAsterisk(integers.get(i),1), // 3*
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "**"
					);
				}
	
				if (parts.length==3){	
					hierarchy.add(integers.get(i),
								  generalizeIntegerByAsterisk(integers.get(i),2), // 33*
								  generalizeIntegerByAsterisk(integers.get(i),1), // 3**
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "***"
					);
				}
	
				if (parts.length==4){	
					hierarchy.add(integers.get(i),
								  generalizeIntegerByAsterisk(integers.get(i),3), // 333*
								  generalizeIntegerByAsterisk(integers.get(i),2), // 33**
								  generalizeIntegerByAsterisk(integers.get(i),1), // 3***
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "****"
					);
				}
	
				if (parts.length==5){	
					hierarchy.add(integers.get(i),
								  generalizeIntegerByAsterisk(integers.get(i),4), // 3333*
								  generalizeIntegerByAsterisk(integers.get(i),3), // 333**
								  generalizeIntegerByAsterisk(integers.get(i),2), // 33***
								  generalizeIntegerByAsterisk(integers.get(i),1), // 3****
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "*****"
					);
				}
	
				if (parts.length==6){	
					hierarchy.add(integers.get(i),
								  generalizeIntegerByAsterisk(integers.get(i),5), // 33333*
								  generalizeIntegerByAsterisk(integers.get(i),4), // 3333**
								  generalizeIntegerByAsterisk(integers.get(i),3), // 333***
								  generalizeIntegerByAsterisk(integers.get(i),2), // 33****
								  generalizeIntegerByAsterisk(integers.get(i),1), // 3*****
//								  generalizeIntegerByInterval(integers.get(i), biggerThan),
								  "******"
					);
				}
			}
			break;
		}
		}
		
		addHierarchy(columnToGeneralize, hierarchy, definitionDS);
	}

	
	public void addHierarchy(Column column, DefaultHierarchy hierarchy, DefinitionServer definition){
		
		for(ColumnServer cs: definition.getColumns()){
			if(cs.getId().equals(column.getId())){
				cs.setHierarchy(hierarchy);
			}
		}
	}


	public DefinitionServer getServerDefinition(DatasetServer dataset, String id){
		for(DefinitionServer definition: dataset.getParameters().getDefinitions()){
			if(definition.getId().equals(id)){
				return definition;
			}
		}
		return null;
	}
	
	public List<String> getAttributeTypesForDefinition(){
		List<String> attributeTypes = new ArrayList<String>();
		attributeTypes.add("identifying");
		attributeTypes.add("sensitive");
		attributeTypes.add("insensitive");
		attributeTypes.add("quasi_identifying");
		attributeTypes.add("unknown");
		
		return attributeTypes;
	}
	
	public String generalizeIntegerByInterval(String integer, int interval){
		String generalizedInteger = "";
		int integerToGeneralize = Integer.parseInt(integer);
		
		if(integerToGeneralize<=interval){
			generalizedInteger = "<="+interval;
		}
		
		if(integerToGeneralize>interval){
			generalizedInteger = ">"+interval;
		}
		
		return generalizedInteger;
	}

	public String generalizeIntegerByAsterisk(String integer, int numOfIntegerToRemain){
	
		
		String[] parts = integer.split("");
		
		String generalizedInteger = "";
		
		switch (numOfIntegerToRemain){
		case 0:{
			if(parts.length==1){
				generalizedInteger = "*";
			}
			if(parts.length==2){
				generalizedInteger = "**";
			}
			if(parts.length==3){
				generalizedInteger = "***";
			}		
			if(parts.length==4){
				generalizedInteger = "****";
			}
			if(parts.length==5){
				generalizedInteger = "*****";
			}
			if(parts.length==6){
				generalizedInteger = "******";
			}
			break;
		}
		case 1:{ // one number to remain
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+"*";
			}
			if(parts.length==3){ // integer is 333 -> 3**
				generalizedInteger = parts[0]+"**";
			}
			if(parts.length==4){
				generalizedInteger = parts[0]+"***";
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+"****";
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+"*****";
			}
			break;
		}
		case 2:{
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+parts[1];
			}
			if(parts.length==3){
				generalizedInteger = parts[0]+parts[1]+"*";
			}		
			if(parts.length==4){
				generalizedInteger = parts[0]+parts[1]+"**";
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+parts[1]+"***";
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+parts[1]+"****";
			}
			break;
		}
		case 3:{
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+parts[1];
			}
			if(parts.length==3){
				generalizedInteger = parts[0]+parts[1]+parts[2];
			}			
			if(parts.length==4){
				generalizedInteger = parts[0]+parts[1]+parts[2]+"*";
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+parts[1]+parts[2]+"**";
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+parts[1]+parts[2]+"***";
			}		
			break;	
		}
		case 4:{
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+parts[1];
			}			
			if(parts.length==3){
				generalizedInteger = parts[0]+parts[1]+parts[2];
			}
			if(parts.length==4){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3];
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+"*";
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+"***";
			}	
			break;
		}
		case 5:{
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+parts[1];
			}
			
			if(parts.length==3){
				generalizedInteger = parts[0]+parts[1]+parts[2];
			}
			if(parts.length==4){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3];
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+parts[4];
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+parts[4]+"*";
			}		
			break;	
		}
		case 6:{
			if(parts.length==1){
				generalizedInteger = parts[0];
			}
			if(parts.length==2){
				generalizedInteger = parts[0]+parts[1];
			}	
			if(parts.length==3){
				generalizedInteger = parts[0]+parts[1]+parts[2];
			}
			if(parts.length==4){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3];
			}
			if(parts.length==5){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+parts[4];
			}
			if(parts.length==6){
				generalizedInteger = parts[0]+parts[1]+parts[2]+parts[3]+parts[4]+parts[5];
			}
			break;
		}
		
		}
		return generalizedInteger;
		
	}

	public int median(List<Integer> m) {
		Collections.sort(m);
	    int middle = m.size()/2;
	    if (m.size()%2 == 1) {
	        return m.get(middle);
	    } else {
	        return (m.get(middle-1) + m.get(middle)) / 2;
	    }
	}
	
	
	/**
	 * 
	 * TESTS
	 */

public void test(){
	 // Define data
    DefaultData data = Data.create();
    data.add("age", "gender", "zipcode");
    data.add("34", "male", "81667");
    data.add("45", "female", "81675");
    data.add("66", "male", "81925");
    data.add("70", "female", "81931");
    data.add("34", "female", "81931");
    data.add("70", "male", "81931");
    data.add("45", "male", "81931");

    // Define hierarchies
    DefaultHierarchy age = Hierarchy.create();
    age.add("34", "<50", "*");
    age.add("45", "<50", "*");
    age.add("66", ">=50", "*");
    age.add("70", ">=50", "*");

    DefaultHierarchy gender = Hierarchy.create();
    gender.add("male", "*");
    gender.add("female", "*");

    // Only excerpts for readability
    DefaultHierarchy zipcode = Hierarchy.create();
    zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****");
    zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****");
    zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****");
    zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****");

    data.getDefinition().setAttributeType("age", age);
    data.getDefinition().setAttributeType("gender", gender);
    data.getDefinition().setAttributeType("zipcode", zipcode);

    // Create an instance of the anonymizer
    ARXAnonymizer anonymizer = new ARXAnonymizer();
    ARXConfiguration config = ARXConfiguration.create();
    config.addCriterion(new KAnonymity(3));
    config.setMaxOutliers(0d);

    ARXResult result;
	try {
		result = anonymizer.anonymize(data, config);

	    // Print info
	    printResult(result, data);

	
	    
	} catch (IOException e) {
		
		e.printStackTrace();
	}


    
    
}

protected static void print(Iterator<String[]> iterator) {
}
protected static void printResult(final ARXResult result, final Data data) {

    // Print time
    final DecimalFormat df1 = new DecimalFormat("#####0.00");
    final String sTotal = df1.format(result.getTime() / 1000d) + "s";

    // Extract
    final ARXNode optimum = result.getGlobalOptimum();
    final List<String> qis = new ArrayList<String>(data.getDefinition().getQuasiIdentifyingAttributes());

    if (optimum == null) {
        return;
    }

    // Initialize
    final StringBuffer[] identifiers = new StringBuffer[qis.size()];
    final StringBuffer[] generalizations = new StringBuffer[qis.size()];
    int lengthI = 0;
    int lengthG = 0;
    for (int i = 0; i < qis.size(); i++) {
        identifiers[i] = new StringBuffer();
        generalizations[i] = new StringBuffer();
        identifiers[i].append(qis.get(i));
        generalizations[i].append(optimum.getGeneralization(qis.get(i)));
        if (data.getDefinition().isHierarchyAvailable(qis.get(i)))
            generalizations[i].append("/").append(data.getDefinition().getHierarchy(qis.get(i))[0].length - 1);
        lengthI = Math.max(lengthI, identifiers[i].length());
        lengthG = Math.max(lengthG, generalizations[i].length());
    }

    // Padding
    for (int i = 0; i < qis.size(); i++) {
        while (identifiers[i].length() < lengthI) {
            identifiers[i].append(" ");
        }
        while (generalizations[i].length() < lengthG) {
            generalizations[i].insert(0, " ");
        }
    }

}

public void test2(){
    // Define public dataset
    DefaultData data = Data.create();
    data.add("identifier", "name", "zip", "age", "nationality", "sen");
    data.add("a", "Alice", "47906", "35", "USA", "0");
    data.add("b", "Bob", "47903", "59", "Canada", "1");
    data.add("c", "Christine", "47906", "42", "USA", "1");
    data.add("d", "Dirk", "47630", "18", "Brazil", "0");
    data.add("e", "Eunice", "47630", "22", "Brazil", "0");
    data.add("f", "Frank", "47633", "63", "Peru", "1");
    data.add("g", "Gail", "48973", "33", "Spain", "0");
    data.add("h", "Harry", "48972", "47", "Bulgaria", "1");
    data.add("i", "Iris", "48970", "52", "France", "1");

    // Define research subset
    @SuppressWarnings("unused")
	DataSubset subset = DataSubset.create(data, new HashSet<Integer>(Arrays.asList(1, 2, 5, 7, 8)));

    // Define hierarchies
    DefaultHierarchy age = Hierarchy.create();
    age.add("18", "1*", "<=40", "*");
    age.add("22", "2*", "<=40", "*");
    age.add("33", "3*", "<=40", "*");
    age.add("35", "3*", "<=40", "*");
    age.add("42", "4*", ">40", "*");
    age.add("47", "4*", ">40", "*");
    age.add("52", "5*", ">40", "*");
    age.add("59", "5*", ">40", "*");
    age.add("63", "6*", ">40", "*");

    DefaultHierarchy nationality = Hierarchy.create();
    nationality.add("Canada", "N. America", "America", "*");
    nationality.add("USA", "N. America", "America", "*");
    nationality.add("Peru", "S. America", "America", "*");
    nationality.add("Brazil", "S. America", "America", "*");
    nationality.add("Bulgaria", "E. Europe", "Europe", "*");
    nationality.add("France", "W. Europe", "Europe", "*");
    nationality.add("Spain", "W. Europe", "Europe", "*");

    DefaultHierarchy zip = Hierarchy.create();
    zip.add("47630", "4763*", "476*", "47*", "4*", "*");
    zip.add("47633", "4763*", "476*", "47*", "4*", "*");
    zip.add("47903", "4790*", "479*", "47*", "4*", "*");
    zip.add("47906", "4790*", "479*", "47*", "4*", "*");
    zip.add("48970", "4897*", "489*", "48*", "4*", "*");
    zip.add("48972", "4897*", "489*", "48*", "4*", "*");
    zip.add("48973", "4897*", "489*", "48*", "4*", "*");

    // Set data attribute types
    data.getDefinition().setAttributeType("identifier", AttributeType.INSENSITIVE_ATTRIBUTE);
    data.getDefinition().setAttributeType("name", AttributeType.INSENSITIVE_ATTRIBUTE);
    data.getDefinition().setAttributeType("zip", zip);
    data.getDefinition().setAttributeType("age", age);
    data.getDefinition().setAttributeType("nationality", nationality);
    data.getDefinition().setAttributeType("sen", AttributeType.INSENSITIVE_ATTRIBUTE);

    // Create an instance of the anonymizer
    ARXAnonymizer anonymizer = new ARXAnonymizer();
    ARXConfiguration config = ARXConfiguration.create();
    config.addCriterion(new KAnonymity(1));
//    config.addCriterion(new DPresence(1d / 2d, 2d / 3d, subset));
    config.setMaxOutliers(0d);

    // Now anonymize
    ARXResult result;
	try {
		result = anonymizer.anonymize(data, config);

	    // Print input
	    System.out.println(" - Input data:");
	    print(data.getHandle().iterator());

	    // Print input
	    System.out.println(" - Input research subset:");
	    print(data.getHandle().getView().iterator());

	    // Print info
	    printResult(result, data);

	    // Print results
	    System.out.println(" - Transformed data:");
	    print(result.getOutput(false).iterator());

	    // Print results
	    System.out.println(" - Transformed research subset:");
	    print(result.getOutput(false).getView().iterator());
	    result.getOutput(false).save("datasets/anonymized/"+"identifier_name_zip_age_nationality_sen"+".csv", ',' );
		System.out.println("Data set anonymized");
	} catch (IOException e) {
		e.printStackTrace();
	}


}

public void test3(){


    // Define data
    DefaultData data = Data.create();
    data.add("zipcode", "age", "disease");
    data.add("47677", "29", "gastric ulcer");
    data.add("47602", "22", "gastritis");
    data.add("47678", "27", "stomach cancer");
    data.add("47905", "43", "gastritis");
    data.add("47909", "52", "flu");
    data.add("47906", "47", "bronchitis");
    data.add("47605", "30", "bronchitis");
    data.add("47673", "36", "pneumonia");
    data.add("47607", "32", "stomach cancer");

    // Define hierarchies
    DefaultHierarchy age = Hierarchy.create();
    age.add("29", "<=40", "*");
    age.add("22", "<=40", "*");
    age.add("27", "<=40", "*");
    age.add("43", ">40", "*");
    age.add("52", ">40", "*");
    age.add("47", ">40", "*");
    age.add("30", "<=40", "*");
    age.add("36", "<=40", "*");
    age.add("32", "<=40", "*");

    // Only excerpts for readability
    DefaultHierarchy zipcode = Hierarchy.create();
    zipcode.add("47677", "4767*", "476**", "47***", "4****", "*****");
    zipcode.add("47602", "4760*", "476**", "47***", "4****", "*****");
    zipcode.add("47678", "4767*", "476**", "47***", "4****", "*****");
    zipcode.add("47905", "4790*", "479**", "47***", "4****", "*****");
    zipcode.add("47909", "4790*", "479**", "47***", "4****", "*****");
    zipcode.add("47906", "4790*", "479**", "47***", "4****", "*****");
    zipcode.add("47605", "4760*", "476**", "47***", "4****", "*****");
    zipcode.add("47673", "4767*", "476**", "47***", "4****", "*****");
    zipcode.add("47607", "4760*", "476**", "47***", "4****", "*****");

    // Define sensitive value hierarchy
    DefaultHierarchy disease = Hierarchy.create();
    disease.add("flu",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease");
    disease.add("pneumonia",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease");
    disease.add("bronchitis",
                "respiratory infection",
                "vascular lung disease",
                "respiratory & digestive system disease");
    disease.add("pulmonary edema",
                "vascular lung disease",
                "vascular lung disease",
                "respiratory & digestive system disease");
    disease.add("pulmonary embolism",
                "vascular lung disease",
                "vascular lung disease",
                "respiratory & digestive system disease");
    disease.add("gastric ulcer",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease");
    disease.add("stomach cancer",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease");
    disease.add("gastritis",
                "stomach disease",
                "digestive system disease",
                "respiratory & digestive system disease");
    disease.add("colitis",
                "colon disease",
                "digestive system disease",
                "respiratory & digestive system disease");
    disease.add("colon cancer",
                "colon disease",
                "digestive system disease",
                "respiratory & digestive system disease");

    data.getDefinition().setAttributeType("age", age);
    data.getDefinition().setAttributeType("zipcode", zipcode);
    data.getDefinition()
        .setAttributeType("disease", AttributeType.SENSITIVE_ATTRIBUTE);

    // Create an instance of the anonymizer
    ARXAnonymizer anonymizer = new ARXAnonymizer();
    ARXConfiguration config = ARXConfiguration.create();
    config.addCriterion(new KAnonymity(3));
    config.addCriterion(new HierarchicalDistanceTCloseness("disease", 0.6d, disease));
    config.setMaxOutliers(0d);

  

    // Now anonymize
    ARXResult result;
	try {
		result = anonymizer.anonymize(data, config);
	    // Print info
	    printResult(result, data);

	    // Process results
	    System.out.println(" - Transformed data:");
	    Iterator<String[]> transformed = result.getOutput(false).iterator();
	    while (transformed.hasNext()) {
	        System.out.print("   ");
	        System.out.println(Arrays.toString(transformed.next()));
	    }
	} catch (IOException e) {
		e.printStackTrace();
	}



}
public void test4(){

    // Define data
    DefaultData data = Data.create();
    data.add("age", "gender", "zipcode", "date");
    data.add("45", "female", "81675", "01.01.1982");
    data.add("34", "male", "81667", "11.05.1982");
    data.add("NULL", "male", "81925", "31.08.1982");
    data.add("70", "female", "81931", "02.07.1982");
    data.add("34", "female", "NULL", "05.01.1982");
    data.add("70", "male", "81931", "24.03.1982");
    data.add("45", "male", "81931", "NULL");
    
    data.getDefinition().setDataType("age", DataType.INTEGER);
    data.getDefinition().setDataType("zipcode", DataType.DECIMAL);
    data.getDefinition().setDataType("date", DataType.DATE);
    
    DefaultHierarchy gender = Hierarchy.create();
    gender.add("male", "*");
    gender.add("female", "*");
    
    // Only excerpts for readability
    DefaultHierarchy zipcode = Hierarchy.create();
    zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****");
    zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****");
    zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****");
    zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****");
    zipcode.add("NULL", "NULL", "NULL", "NULL", "NULL", "*****");
    
    data.getDefinition().setAttributeType("age", MicroAggregationFunction.createGeometricMean());
    data.getDefinition().setAttributeType("gender", gender);
    data.getDefinition().setAttributeType("zipcode", zipcode);
    data.getDefinition().setAttributeType("date", MicroAggregationFunction.createArithmeticMean());
    
    // Create an instance of the anonymizer
    ARXAnonymizer anonymizer = new ARXAnonymizer();
    ARXConfiguration config = ARXConfiguration.create();
    config.addCriterion(new KAnonymity(2));
    config.setMaxOutliers(0.5d);

    // Obtain result
    ARXResult result;
	try {
		result = anonymizer.anonymize(data, config);
	    // Print info
	    printResult(result, data);

	    // Process results
	    System.out.println(" - Transformed data:");
	    Iterator<String[]> transformed = result.getOutput(false).iterator();
	    while (transformed.hasNext()) {
	        System.out.print("   ");
	        System.out.println(Arrays.toString(transformed.next()));
	    }
	} catch (IOException e) {
		e.printStackTrace();
	}

}

public void test5(){
    aggregate(new String[]{"xaaa", "xxxbbb", "xxcccc"}, DataType.STRING);
    aggregate(new String[]{"xaaa", "xxxbbb", "xxcccc"}, DataType.STRING);
    aggregate(new String[]{"1", "2", "5", "11", "12", "3"}, DataType.STRING);
    aggregate(new String[]{"1", "2", "5", "11", "12", "3"}, DataType.INTEGER);


/**
 * 
 *
 * @param args
 * @param type
 */

}
public void aggregate(String[] args, DataType<?> type){
    
    AggregateFunctionBuilder<?> builder = type.createAggregate();
    System.out.println("Input: "+Arrays.toString(args) + " as "+type.getDescription().getLabel()+"s");
    System.out.println(" - Set                         :"+builder.createSetFunction().aggregate(args));
    System.out.println(" - Set of prefixes             :"+builder.createSetOfPrefixesFunction().aggregate(args));
    System.out.println(" - Set of prefixes of length 2 :"+builder.createSetOfPrefixesFunction(2).aggregate(args));
    System.out.println(" - Common prefix               :"+builder.createPrefixFunction().aggregate(args));
    System.out.println(" - Common prefix with redaction:"+builder.createPrefixFunction('*').aggregate(args));
    System.out.println(" - Bounds                      :"+builder.createBoundsFunction().aggregate(args));
    System.out.println(" - Interval                    :"+builder.createIntervalFunction().aggregate(args));
    System.out.println();
}
}

