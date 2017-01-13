package com.arx.springmvcangularjs.controller;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.arx.springmvcangularjs.beans.algorithm.AlgorithmType;
import com.arx.springmvcangularjs.beans.dataset.DatasetClient;
import com.arx.springmvcangularjs.beans.parameter.ParameterClient;
import com.arx.springmvcangularjs.beans.property.Properties;
import com.arx.springmvcangularjs.service.DatasetServiceImpl;

@Controller
@RequestMapping("/dataset")
public class DatasetController {

    @Autowired
    private DatasetServiceImpl datasetService;

    @RequestMapping(value="/connectTo/{ip}/ip")
    public @ResponseBody String connectTo(@PathVariable("ip") String ip){
    	String localPeerId = datasetService.getActivePeer().getId();
    	return datasetService.getDm().getManager().addNewPeer(localPeerId, ip).getId();
    
    }
    
    @RequestMapping(value="/returnPeerId")
    public @ResponseBody String returnPeerId(@RequestBody String remotePeerId, HttpServletRequest request){
    	if(request.getRemoteAddr().toString().equals("127.0.0.1")){
    		String [] parts = remotePeerId.split(" ");
    		String remoteId = parts[0];
    		String ip = parts[1];
    		datasetService.getDm().getManager().addPeerClient(remoteId, ip);
        	
        	return datasetService.getActivePeer().getId();
    	}
    	return "no_access";
    }
    
    @RequestMapping(value="/utilityAndRisk")
    public @ResponseBody Object[][] makeUtilityAndRiskAnalysis(@RequestBody ParameterClient parameters) {
     	return datasetService.getDm().anonymizeDatasetByParameters(parameters);
    }
    
    @RequestMapping(value="/getSimilarDatasets")
    public @ResponseBody String[][] getSimilarDatasets(@RequestBody Properties properties){
    	return datasetService.getDm().getDp().getSimilarDatasets(properties);
    }
    
    @RequestMapping("/getPeersInNetwork")
    public @ResponseBody List<String> fetchPeers() {
    	return datasetService.getManager().getAllPeersId();
    }
    
    @RequestMapping("datasetlist.json")
    public @ResponseBody List<DatasetClient> fetchDatasets() {
    	return datasetService.getAllDatasets();
    }
    
    @RequestMapping("/getParameters/{id}") 
    public @ResponseBody ParameterClient fetchParameters(@PathVariable("id") String id){
    	return datasetService.getParameters(id);
    }
    @RequestMapping("foldercontentlist.json")
    public @ResponseBody List<File> readFilesOnSystem() {
    	return datasetService.readFilesOnSystem("datasets/");    	
    }
    
    @RequestMapping("attributetypeoptions.json")
    public @ResponseBody List<String> getAttibuteTypeOptions() {
    	return datasetService.getAttributeTypeOptions();
    }
    
    @RequestMapping("/getAlgorithmCriteria/{id}")
    public @ResponseBody List<String> getAlgorithmCriteria(@PathVariable("id") String id) {
    	return datasetService.getAlgorithmCriteria(id);
    }
    
    @RequestMapping("/get/{id}")
    public @ResponseBody DatasetClient getDataset(@PathVariable("id") String id) {
    	return datasetService.getDataset(id);
    }
    
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody String addNewDataset(@RequestBody DatasetClient dataset) {
        return datasetService.addNewDataset(dataset.getProperties().getPath(), dataset.getProperties().getName());
    }
    
    @RequestMapping("/anonymize/{id}")
    public @ResponseBody void anonymize(@PathVariable("id") String id) {
        datasetService.anonymizeDataset(id);
    }
    
    @RequestMapping(value = "/remove/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void removeDataset(@PathVariable("id") String id) {
        datasetService.removeDataset(id);
    }

    @RequestMapping(value = "/removeAll", method = RequestMethod.DELETE)
    public @ResponseBody void removeAllDatasets() {
        datasetService.removeAllDatasets();
    }
    
    @RequestMapping("/removeDefinition/{datasetId}/{definitionId}")
    public @ResponseBody void removeDefiniton(@PathVariable("datasetId") String datasetId, @PathVariable("definitionId") String definitionId){
    	datasetService.removeDefinition(datasetId, definitionId);
    }
    @RequestMapping(value = "/algorithm/submit/kanonymity/", method = RequestMethod.POST)
    public @ResponseBody void submitAlgorithmKAnonymity(@RequestBody ParameterClient parameters){
    	datasetService.createAnonymizationAlgorithm(parameters.getIdDataset(), AlgorithmType.getK_ANONYMITY(), parameters);
    }
    
    @RequestMapping(value = "/algorithm/submit/ldiversity/", method = RequestMethod.POST)
    public @ResponseBody void submitAlgorithmLDiversity(@RequestBody ParameterClient parameters){
    	datasetService.createAnonymizationAlgorithm(parameters.getIdDataset(), AlgorithmType.getL_DIVERSITY(), parameters);
    }
    
    @RequestMapping(value = "/algorithm/submit/tcloseness/", method = RequestMethod.POST)
    public @ResponseBody void submitAlgorithmTCloseness(@RequestBody ParameterClient parameters){
    	datasetService.createAnonymizationAlgorithm(parameters.getIdDataset(), AlgorithmType.getT_CLOSENESS(), parameters);
    }
    
    @RequestMapping(value = "/select/algorithm/{datasetId}/{algorithmId}")
    public @ResponseBody void selectAlgorithm(@PathVariable("datasetId") String datasetId, @PathVariable("algorithmId") String algorithmId){
    	datasetService.selectAlgorithm(datasetId, algorithmId);
    }
    
    
    @RequestMapping(value = "/submitAttributeTypes", method = RequestMethod.POST)
    public @ResponseBody void submitAttributeTypes(@RequestBody ParameterClient parameters){
    	datasetService.submitNewDefinition(parameters);
    }
    
    @RequestMapping(value = "/select/definition/{datasetId}/{definitionId}")
    public @ResponseBody void selectDefinition(@PathVariable("datasetId") String datasetId, @PathVariable("definitionId") String definitionId){
    	datasetService.selectDefinition(datasetId, definitionId);
    }
    
    @RequestMapping(value = "/submitHierarchyNoImport/{id}/{columnName}", method = RequestMethod.POST)
    public @ResponseBody void createHierarchyAutoNoImport(@RequestBody List<String> param){    	
    	datasetService.createHierarchyAutoNoImport(param);
    }
    
    @RequestMapping(value = "/submitHierarchy/auto/{id}")
    public@ResponseBody void createHierarchyAutoImport(@RequestBody List<String> param){  
    	//
    }
    
    @RequestMapping(value = "/submitHierarchy/manually/{id}")
    public @ResponseBody void createHierarchyMan(@RequestBody List<String> param){  
    	datasetService.createHierarchyMan(param);
    }
    
    @RequestMapping("/layout")
    public String getDatasetPartialPage() {
        return "dataset/layout";
    }
    
    @RequestMapping("/import/layout")
    public String getDatasetImportPartialPage() {
    	return "dataset/import/layout";
    }
    
    @RequestMapping("/preview/layout")
    public String getDatasetPreviewPartialPage() {
    	return "dataset/preview/layout";
    }
    
    @RequestMapping("/result/layout")
    public String getDatasetResultPartialPage() {
    	return "dataset/result/layout";
    }
    
    @RequestMapping("/configuration/layout")
    public String getDatasetConfigurationPartialPage() {
    	return "dataset/configuration/layout";
    }
}
