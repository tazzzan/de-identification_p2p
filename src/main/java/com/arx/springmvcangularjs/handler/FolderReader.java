package com.arx.springmvcangularjs.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderReader {

	public List<File> returnFildesInFolder(String path){
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		List<File> listToReturn = new ArrayList<File>();
		for(File file: listOfFiles){
			if (file.isHidden()==false && file.isFile()){
				listToReturn.add(file);
			}
		}
		
		return listToReturn;
	}
}
