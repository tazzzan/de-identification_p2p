package com.arx.springmvcangularjs.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.DataSource;
import org.deidentifier.arx.DataType;

import com.arx.springmvcangularjs.beans.DefinitionClient;
import com.arx.springmvcangularjs.beans.column.Column;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.dataset.DatasetP2P;
import com.arx.springmvcangularjs.beans.dataset.DatasetServer;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 'DataImporter' imports Data into a new Dataset
 * -> supports CSV, XLS and XLSX (DB [as JDBC] and XML not yet supported)
 * 
 * importDataIntoNewDataset(path, name)
 * addColumns(source, columnNames, columnTypes)
 * loadAppropriateDataSource(path, format)
 * checkFormat(path) 
 * getColumnNames(path)
 * getColumnTypes(path)
 * 
 * @author Ilja Lichtenberg
 *
 */

@SuppressWarnings("rawtypes")
public class DataImporter {
	
	Transformer dt = new Transformer();
	
	String format;
	List<DataType> columnTypes;
	List<String> columnNames;
	char separator = ',';
	char quoteCharacter = '\'';
	
	// Constructors
	public DataImporter() {};
	

	// Functions
	public DatasetServer importDataIntoNewDataset (String path, String name, DatasetProvider dp) {
		
		// Get information about (not yet imported !!) Data
		format = checkFormat(path);
		if(format.equals("CSV")){
			checkSeparatorInCSV(path);
			checkQuoteCharacterInCSV(path);
		}
				
		columnNames = getColumnNames(path, format);
		columnTypes = getColumnTypes(path, format);
		
		DataSource source = loadAppropriateDataSource(path, format);
				
		source = addColumns(source, columnNames, columnTypes);
				
		// At the moment without own method
		List<Column> columns = new ArrayList<Column>();
		for(int i=0; i<columnNames.size(); i++){
			Column column = new Column();
			column.setName(columnNames.get(i));
			column.setColumnType(columnTypes.get(i).toString());
			columns.add(column);
		}
		
		DatasetServer dataset;
		try {
			Data data = Data.create(source);
			
			dataset = createDataset(dp, data,
						  path, name, columns,
						  data.getHandle().getNumRows(), data.getHandle().getNumColumns());
						
		}
		catch (IOException e) {throw new RuntimeException(e);}
		
		return dataset;
	}
	
	public DatasetServer createDataset(DatasetProvider dp, Data data, String path, String name,
							  List<Column> columns, int rowNum, int columnNum){
		
		String commonId = UUID.randomUUID().toString();
		
	
		DatasetServer datasetServer = new DatasetServer(commonId);
		DatasetClient datasetClient = new DatasetClient(commonId);
		DatasetP2P datasetP2P = new DatasetP2P(commonId);

		datasetClient.setData(dt.returnDataIn2DArray(data));
		datasetClient.getProperties().setPath(path);
		datasetClient.getProperties().setName(name);
		datasetClient.getProperties().setRowNum(rowNum);
		datasetClient.getProperties().setColumnNum(columnNum);
		datasetClient.getProperties().setColumns(columns);	
		datasetClient.getParameters().getDefinitions().add(new DefinitionClient("0", dt.returnColumnClientList(columns)));
		datasetClient.getParameters().setSettedDefinitionId("0");
		
		datasetServer.setData(data);
		datasetServer.getProperties().setPath(path);
		datasetServer.getProperties().setName(name);
		datasetServer.getProperties().setRowNum(rowNum);
		datasetServer.getProperties().setColumnNum(columnNum);
		datasetServer.getProperties().setColumns(columns);
		
		/**
		 * missing vector specialization for data
		 */
		datasetP2P.getProperties().setPath(path);
		datasetP2P.getProperties().setName(name);
		datasetP2P.getProperties().setRowNum(rowNum);
		datasetP2P.getProperties().setColumnNum(columnNum);
		datasetP2P.getProperties().setColumns(columns);
		
		dp.add(datasetClient);
		dp.add(datasetServer);
		dp.add(datasetP2P);
		
		return datasetServer;
	}
	
	public DataSource addColumns(DataSource source, List<String> columnNames, List<DataType> columnTypes) {
		
		for (int i=0; i<columnNames.size(); i++) {
			source.addColumn(columnNames.get(i), columnTypes.get(i));
		}
		return source;
	}

	// NOTE: DB and XML not yet supported
	public DataSource loadAppropriateDataSource(String path, String format){
		DataSource source = null;
		
		switch(format) {
		case "CSV":
			source = DataSource.createCSVSource(path,StandardCharsets.UTF_8, separator, true);
			break;
		case "XLS":
			source = DataSource.createExcelSource(path, 0, true);
			break;
		case "XLSX":
			source = DataSource.createExcelSource(path, 0, true);
			break;
		case "DB":
			try {
				source = DataSource.createJDBCSource(path, path);
			} 
			catch (SQLException e1) {e1.printStackTrace();}
			break;
		case "XML":
		}
		
		return source;
	}
	
	// NOTE: DB and XML not yet supported
	public String checkFormat(String path) {
		
		String format = "";
		
		if(path.matches(".*\\.[Cc][Ss][Vv]")) {
			format="CSV";
		}
		if(path.matches(".*\\.[Xx][Ll][Ss]")) {
			format="XLS";
		}
		if(path.matches(".*\\.[Xx][Ll][Ss]\\D+")) {
			format="XLSX";
		}
		if(path.matches(".*\\.[Xx][Mm][Ll]")) {
			format="XML";
		}
		if(format.equals("")) {
		}
		return format;
	}
	
	public List<String> getColumnNames(String path, String format){
		List<String> columnNames = new ArrayList<String>();
		
		try {
	        FileInputStream file = new FileInputStream(new File(path));
	        switch(format){
		    case "XLS":{
	            org.apache.poi.ss.usermodel.Workbook workbook = null;
	    		try { 
	    			workbook = WorkbookFactory.create(file);
	    		} 
	    		catch (InvalidFormatException e){e.printStackTrace();}		
	            // Get first /desired(MUST BE IMPLEMENTED) sheet from the workbook
	            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
		            
	            Row row = sheet.getRow(sheet.getFirstRowNum());
	            Iterator<Cell> cellIterator = row.cellIterator();
		        while (cellIterator.hasNext()) {
		          	Cell cell = cellIterator.next();
		            columnNames.add(cell.getStringCellValue()); 
		        }
		       	break;
		    }
		    case "XLSX":{
		       	XSSFWorkbook workbook = new XSSFWorkbook(file);
		       	XSSFSheet sheet = workbook.getSheetAt(0);

		        Row row = sheet.getRow(sheet.getFirstRowNum());
		        Iterator<Cell> cellIterator = row.cellIterator();
		        while (cellIterator.hasNext()) {
		           	Cell cell = cellIterator.next();
		            columnNames.add(cell.getStringCellValue()); 
		        }
		      	break;
		    }
		    case "CSV":{
		        // Quote character is singe quote
		        CSVReader reader = new CSVReader(new FileReader(path), separator, quoteCharacter);		             
		        String[] firstRow = reader.readNext();
		        for(int i=0; i<firstRow.length; i++) {
		          	columnNames.add(firstRow[i]);
		        } 
		        reader.close();
		        break;
	        }
	        }  
        file.close();
		} 
		catch (IOException e) {e.printStackTrace();}
            
		return columnNames;
	}
	
	public List<DataType> getColumnTypes(String path, String format) {
		List<DataType> columnTypes = new ArrayList<DataType>();
		
		try {
	        FileInputStream file = new FileInputStream(new File(path));
	        switch(format){
	        case "XLS":{
	            // Create Workbook instance
	            org.apache.poi.ss.usermodel.Workbook workbook = null;
	    		try { workbook = WorkbookFactory.create(file);} catch (InvalidFormatException e) 
	    		{e.printStackTrace();}
	            // Get first /desired(MUST BE IMPLEMENTED) sheet from the workbook
	            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
	            
		        Row row = sheet.getRow(sheet.getFirstRowNum()+1);
	            Iterator<Cell> cellIterator = row.cellIterator();
	            while (cellIterator.hasNext()) {
			        Cell cell = cellIterator.next();
			       	switch (cell.getCellType()) {
		            case Cell.CELL_TYPE_NUMERIC:
		            	columnTypes.add(DataType.INTEGER);
		            	break;
		            case Cell.CELL_TYPE_STRING:
		            	columnTypes.add(DataType.STRING);
		            	break;
		            }          
			    }
	            break;
	        }
	        case "XLSX":{
	        	XSSFWorkbook workbook = new XSSFWorkbook(file);
	        	XSSFSheet sheet = workbook.getSheetAt(0);
	        	
	        	Row row = sheet.getRow(sheet.getFirstRowNum()+1);

	            Iterator<Cell> cellIterator = row.cellIterator();
			    while (cellIterator.hasNext()) {
			        Cell cell = cellIterator.next();
			       	switch (cell.getCellType()) {
		            case Cell.CELL_TYPE_NUMERIC:
		            	columnTypes.add(DataType.INTEGER);
		            	break;
		            case Cell.CELL_TYPE_STRING:
		            	columnTypes.add(DataType.STRING);
		            	break;
		            }          
			    }
			    break;
	        }
	        case "CSV":{
	        	// Separator is comma
	            // Quote character is singe quote
	            // Start reading from line number 2 (line numbers start from zero)

	            CSVReader reader = new CSVReader(new FileReader(path), separator, quoteCharacter, 1);
	            
	            String[] secondRow = reader.readNext();
	          	for(int i=0; i<secondRow.length; i++){	
	    			if(secondRow[i].matches("[0-9]+")){
	    				columnTypes.add(DataType.INTEGER);
	    			}
	    			// if I had setted to "DataType.INTEGER" in case of e.g.'<40' than throws
	    			// IllegalArgumentException("Data value does not match data type")
//	    			if(secondRow[i].matches("\\<?\\>?\\=?\\d*")){
//	    				columnTypes.add(DataType.STRING);
//		    		}
	    			if(secondRow[i].matches("[^0-9]+")){
	    				columnTypes.add(DataType.STRING);
	    			}
	    			// for the case e.g.'<=40'
	    			if(secondRow[i].matches("[^0-9]+[^0-9]*[0-9]+.*")){

	    				columnTypes.add(DataType.STRING);
	    			}
	    			// for the case e.g.'x40!x'
	    			if(secondRow[i].matches(".*[0-9]+[^0-9]+.*")){
	    				columnTypes.add(DataType.STRING);
	    			}
	    		}
	          	reader.close();
	          	break;
	        }
		    }
		    file.close();
		} 
		catch (IOException e) {e.printStackTrace();}    
		
		return columnTypes;
	}
	
	public void checkSeparatorInCSV(String path){
        FileReader reader;
		try {
			reader = new FileReader(path);
			BufferedReader br = new BufferedReader(reader);
	        String firstRow = br.readLine();
	        String secondRow = br.readLine();
	        
    		if(firstRow.matches(".*\\;+.*")){	separator = ';'; }
    		if(firstRow.matches(".*\\,+.*")){	separator = ','; }
    		if(firstRow.matches(".*\\#+.*")){	separator = '#'; }
    		if(firstRow.matches(".*\t+.*")){	separator = '|'; }
    			
			// if another separator is used in the sccond line than --> '?' 
			// must be defined on its own though
    		char separatorTest = separator; 
    		
     		if(secondRow.matches(".*\\;+.*")){	separatorTest = ';'; }
    		if(secondRow.matches(".*\\,+.*")){	separatorTest = ','; }
    		if(secondRow.matches(".*\\#+.*")){	separatorTest = '#'; }
    		if(secondRow.matches(".*\t+.*")){	separatorTest = '|'; }
    			
			if(separator!=separatorTest){	separator = '?'; }	

			
    		br.close();
	        
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }		             
	}
	
	public void checkQuoteCharacterInCSV(String path){
        FileReader reader;
		try {
			reader = new FileReader(path);
			BufferedReader br = new BufferedReader(reader);
			@SuppressWarnings("unused")
			String firstRow = br.readLine();
	        String secondRow = br.readLine();
	        String thirdRow = br.readLine();
	        
	        if(secondRow.matches(".*\".*\".*")){	quoteCharacter = '"'; }
			if(secondRow.matches(".*\'.*\'.*")){	quoteCharacter = '\''; }
			
			// if another quote character is used in the third line than --> '?' 
			// must be defined on its own though
    		char quoteCharacterTest = quoteCharacter; 

	        if(thirdRow.matches(".*\".*\".*")){	quoteCharacter = '"'; }
			if(thirdRow.matches(".*\'.*\'.*")){	quoteCharacter = '\''; }
			
			if(quoteCharacter!=quoteCharacterTest) { quoteCharacterTest = '?'; };
					
			
    		br.close();
	        
		} 
		catch (FileNotFoundException e) { e.printStackTrace(); }
		catch (IOException e) { e.printStackTrace(); }		
           
	}

	public Transformer getDt() {
		return dt;
	}

	public void setDt(Transformer dt) {
		this.dt = dt;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public List<DataType> getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(List<DataType> columnTypes) {
		this.columnTypes = columnTypes;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	public char getQuoteCharacter() {
		return quoteCharacter;
	}

	public void setQuoteCharacter(char quoteCharacter) {
		this.quoteCharacter = quoteCharacter;
	}
}
