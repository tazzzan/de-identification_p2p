App.controller('DataSetController', function($scope, $http, $filter) {

    /**
	 * Peer_to_peer
	 *
     */
	$scope.remoteUserIp = "";
	
	$scope.peersList =  [];


    /**
	 * Dataset
	 *
     */

    
    $scope.dataset = {};   
    $scope.dataset.properties = {};
    $scope.datasetSelected;
    $scope.datasets =[];

    $scope.custom = true;


    $scope.selectedDatasetPath;
    $scope.datasetsInFolder = [];
    
    $scope.columnsFromDefinition = ["moin","noin"]; 
    $scope.columnsFromDefinition1 = [];
    
    $scope.attributeTypeOptions = ['none'];
        
    
    $scope.selectDefinition = function(dataset, definitionId){
    	
    	angular.forEach(dataset.parameters.definitions, function(definition){
    		if(definition.id===definitionId){
    			dataset.definitionSelected = definition;
    			dataset.parameters.settedDefinitionId = definition.id;
    		}
    		if(definition.id===dataset.parameters.settedDefinitionId){
    			dataset.definitionSelected = definition;
    		}
    	})
    }
  
    function Dataset(dataset){
    	this.data = dataset.data;
    	this.properties = dataset.properties;
    	this.parameters = dataset.parameters;
    	this.results = dataset.results;   	
    	this.similarDatasets = dataset.similarDatasets;
    	
    	this.algorithmCriteria = [];
    	this.columnSelected = {};
    	this.definitionSelected = {};
    	    	
        this.setHierarchy = {};
        this.setHierarchy.attributesSubmitted = false;
        
        this.kAnonymityIsTrue = true;        
    }

    $scope.selectDataset = function(dataset) {
    	$scope.datasetSelected=dataset;
    	$scope.selectDefinition(dataset);
    }

    $scope.getDatasetFromWorkspace = function(datasetId){
        angular.forEach($scope.datasets, function(dataset){
            if(dataset.properties.id==datasetId){
                return dataset;
            }
        })
    }


    $scope.isThereAnyDatasets = function() {
        return typeof $scope.datasets[0] === 'undefined';
    }

    $scope.idDataset = function(datasetId){
        var reply = datasetId;
        angular.forEach($scope.datasets, function(dataset){
            if(dataset.properties.id==datasetId){
                reply = "me";
            }
        })
        return reply;
    }

    $scope.checkIfSelected = function(dataset){
        if(dataset === $scope.datasetSelected)
            return true;
    }

    $scope.checkIfDefinitionSelected = function(definition){
    	if(definition === $scope.datasetSelected.definitionSelected)
    		return true;
    }

    $scope.selectDatasetPathInSystem = function(path) {
    	$scope.dataset.properties.path = path;
    }
    
 
    $scope.checkAttributeType = function(column){
    	if(column.attributeType==="quasi_identifying"){
    		column.angularParameters.hierarchyIsTrue = true;
    		column.angularParameters.algorithmIsSensitive = false;
    	}    	
    	if(column.attributeType==="sensitive"){
    		column.angularParameters.hierarchyIsTrue = false;
    		column.angularParameters.algorithmIsSensitive = true;
    	}
    	if(column.attributeType==="insensitive"){
    		column.angularParameters.hierarchyIsTrue = false;
    		column.angularParameters.algorithmIsSensitive = false;
    	}
       	if(column.attributeType==="identifying"){
       		column.angularParameters.hierarchyIsTrue = false;
       		column.angularParameters.algorithmIsSensitive = false;
    	}
    	$scope.selectColumn(column);
    }
	
    $scope.selectColumn = function(column){
    	$scope.datasetSelected.columnSelected = column; 
    	
    	if($scope.isHierarchySubmitted(column)){
    		column.angularParameters.hierarchySubmitted = false;
    	}
    }
    
    $scope.isColumnSelected = function(){
    	if($scope.datasetSelected.columnSelected.name !== undefined){
    		return true;
    	}
    	else
    		return false;
    }
    
    $scope.isQIColumnSelected = function(){
    	if($scope.isColumnSelected()){
    		if($scope.datasetSelected.columnSelected.angularParameters.hierarchyIsTrue === true){
    			return true;
    		}
    		if($scope.datasetSelected.columnSelected.angularParameters.hierarchyIsTrue === false) {
    			return false;
    		}
    	}
    }
    
    $scope.isHierarchySubmitted = function(column){
    	if(column.attributeType === "quasi_identifying"){
        	if(column.angularParameters.hierarchySubmitted === true){
        		return true;
        	}
    	}
    	if(column.attributeType !== "quasi_identifying"){
    		return true;
    	}
    }
    
    $scope.isSensitiveSubmitted = function(column){
    	if(column.attributeType === "sensitive"){
        	if(column.angularParameters.algorithmIsSensitiveSubmitted === true){
        		return true;
        	}
    	}
    	if(column.attributeType !== "sensitive"){
    		return true;
    	}
    }
    
    
    $scope.setHierarchyIsAutomatic = function(boolean){
    	$scope.datasetSelected.columnSelected.angularParameters.hierarchyIsAutomatic = boolean;
    }
    
      
    $scope.containsQuasiIdentifying = function(attributeType) {
      	if(attributeType === "quasi_identifying"){
       		return true;
        }
        else{
        	return false;
        }
    }
    
    $scope.containsSensitive = function(attributeType) {
      	if(attributeType === "sensitive"){
       		return true;
        }
        else{
        	return false;
        }
    }

    $scope.selectLDiversity = function() {
    	$scope.datasetSelected.columnSelected.angularParameters.algorithmIsLDiversity=true;
    	$scope.datasetSelected.columnSelected.angularParameters.algorithmIsTCloseness=false;
    	$scope.datasetSelected.kAnonymityIsTrue=false;
    }
    
    $scope.selectTCloseness = function() {
    	$scope.datasetSelected.columnSelected.angularParameters.algorithmIsLDiversity=false;
    	$scope.datasetSelected.columnSelected.angularParameters.algorithmIsTCloseness=true;
    	$scope.datasetSelected.kAnonymityIsTrue=false;
    }
    
    $scope.isEmpty = function (obj) {
        for (var i in obj) {
	        if (obj.hasOwnProperty(i)){
	        	return false;
	        }
	        return true;
        }
    };
    
    $scope.correctFetchedDatasets = function(existingDatasets, fetchedDatasets){
    	$scope.newDatasetList = [];
    	
    	angular.forEach(fetchedDatasets, function(fetchedDataset){
    		var angularDataset = new Dataset(fetchedDataset);

    		if(existingDatasets[0]!==undefined){
    			angular.forEach(existingDatasets, function(existingDataset){ 
        			if(fetchedDataset.properties.id===existingDataset.properties.id){
    					angularDataset.setHierarchy = existingDataset.setHierarchy;
    					angularDataset.definitionSelected = existingDataset.definitionSelected;
    					angularDataset.setAlgorithm = existingDataset.setAlgorithm;
//    			    	angularDataset.algorithmCriteria = existingDataset.algorithmCriteria;
    			    	angularDataset.columnSelected = existingDataset.columnSelected;
    			    	angularDataset.setHierarchy.attributesSubmitted = existingDataset.setHierarchy.attributesSubmitted;
        			}
        		})
    		}
			$scope.newDatasetList.push(angularDataset);
    	})
    	return $scope.newDatasetList;
    }
    
    
    /**
     * AJAX CALLS
     */    

	$scope.fetchDatasets = function(datasetId){
		$http.get('dataset/datasetlist.json').success(function(datasets){
        	$scope.fetchedDatasets = $scope.correctFetchedDatasets($scope.datasets, datasets);
          
        	if($scope.datasetSelected !== undefined){
            	angular.forEach($scope.fetchedDatasets, function(fetchedDataset){
            		if(fetchedDataset.properties.id===$scope.datasetSelected.properties.id){
            			$scope.selectDataset(fetchedDataset);
            		}
            	})
            }

        	if($scope.datasetSelected === undefined){
        		var length = $scope.fetchedDatasets.length;
        		if(length!==0){
            		$scope.selectDataset($scope.fetchedDatasets[length-1]);
        			
        		}
        	}
        	
        	
        	angular.forEach($scope.fetchedDatasets, function(dS){
          		var dsId = "\""+dS.properties.id+"\"";
        		if(dsId === datasetId){
        			$scope.selectDefinition(dS, "0");
        			$scope.selectDataset(dS);       			
        		}
        	});  
        	$scope.datasets = $scope.fetchedDatasets;
            $scope.fetchDatasetsInFolder();
        });
	};


    $scope.fetchDatasetsInFolder = function() {
        $http.get('dataset/foldercontentlist.json').success(function(datasetsInFolder){
            $scope.datasetsInFolder = datasetsInFolder;
        });
    };

	$scope.fetchDatasetsInFolder = function() {
    	$http.get('dataset/foldercontentlist.json').success(function(datasetsInFolder){
    		$scope.datasetsInFolder = datasetsInFolder;
    	});
    };
    
    $scope.fetchParameters = function(dataset) {
    	$http.get('dataset/getParameters/' + dataset.properties.id).success(function(datasetParameters){
    		dataset.parameters = datasetParameters;
        	$scope.selectDefinition(dataset);
    	});
    };
    
    $scope.getDataset = function(id) {
        $http.get('dataset/get/' + id).success(function(dataset) {
            return dataset;
        });
    };
    

    $scope.addNewDataset = function(dataset) {
    	$http.post('dataset/add/', dataset).success(function(datasetId) {
    		$scope.fetchDatasets(datasetId);
    	});		 
    	$scope.dataset = {};
    	$scope.getAttributeTypeOptions(); 
    	$scope.getPeersInNetwork();    	
    };
    	
    $scope.removeDataset = function(dataset) {
        $http.delete('dataset/remove/' + dataset.properties.id).success(function() {
        	if($scope.datasetSelected.properties.id===dataset.properties.id){
        		$scope.datasetSelected=undefined;
        	}
        	$scope.fetchDatasets();
        });
    };

    $scope.removeAllDatasets = function() {
        $http.delete('dataset/removeAll').success(function() {
        	$scope.datasetSelected=undefined;
            $scope.fetchDatasets();
        });
    };
    
    
    $scope.submitAlgorithmKAnon = function(parameters) {    	
    	$http.post('dataset/algorithm/submit/kanonymity/', parameters).success(function() {
    		alert("algorithm kanonymity submitted");
    		$scope.fetchParameters(dataset);
    	});
    	
    };
    
    $scope.submitAlgorithmLDiv = function(dataset) {
		$scope.datasetSelected.columnSelected.angularParameters.algorithmIsSensitiveSubmitted = true;
    	$http.post('dataset/algorithm/submit/ldiversity/', dataset.parameters).success(function() {
    		alert("algorithm ldiversity submitted");
    		$scope.fetchParameters(dataset);
    	});
    };
    
    $scope.submitAlgorithmTClo = function(dataset) { 	
    	$scope.datasetSelected.columnSelected.angularParameters.algorithmIsSensitiveSubmitted = true;
       	$http.post('dataset/algorithm/submit/tcloseness/', dataset.parameters).success(function() {
        	alert("algorithm tcloseness submitted");
    		$scope.fetchParameters(dataset);
       });
    };
    
    $scope.anonymizeDataset = function(dataset) {
        $http.post('dataset/anonymize/' + dataset.properties.id).success(function() {
        	$scope.fetchDatasets();
    		$scope.fetchParameters(dataset);
        	$scope.getAlgorithmCriteria(dataset);
        });
    };
    
    
    $scope.submitAttributeTypes = function(dataset){
    	$http.post('dataset/submitAttributeTypes/', dataset.parameters).success(function(){
    		dataset.setHierarchy.attributesSubmitted = true;  
    		$scope.fetchParameters(dataset);
    	});
    	 	
    };
    
    $scope.submitHierarchyToAuto = function(column){
    	column.angularParameters.hierarchySubmitted = true;
    };
    
    $scope.createHierarchyAutoImport = function(path, id){
    	$http.post('dataset/submitHierarchy/auto/' + id, path).success(function() {

    		$scope.datasetSelected.columnSelected.angularParameters.hierarchySubmitted = true;
    	});
    };

    $scope.createHierarchyMan = function(id,
    									 column,
    									 min,
    									 max,
    									 biggerThan){
    	if(min==undefined){	min = 0;}
    	if(max==undefined){	max = 0;}
    	if(biggerThan==undefined){	biggerThan = 0;}
    	
    	var selectionParams = [];
    	selectionParams.push(id);
    	selectionParams.push(column.name);
    	selectionParams.push(min);
    	selectionParams.push(max);
    	selectionParams.push(biggerThan);
    	
    	$http.post('dataset/submitHierarchy/manually/' + id, selectionParams).success(function() {
    		alert("hierarchy created")
    		$scope.datasetSelected.columnSelected.angularParameters.hierarchySubmitted = true;
    	});
    };

    $scope.getAttributeTypeOptions = function() {
    	$http.get('dataset/attributetypeoptions.json').success(function(attributeTypeOptions) {
    		$scope.attributeTypeOptions = attributeTypeOptions;
    	});
    };
    
    $scope.removeDefinition = function(dataset, definitionId){
    	$http.get('dataset/removeDefinition/'+ dataset.properties.id + '/'+ definitionId).success(function(){
    		$scope.fetchParameters(dataset);
    	});
    };
    
    $scope.getAlgorithmCriteria = function(dataset) {
    	$http.get('dataset/getAlgorithmCriteria/' + dataset.properties.id).success(function(algorithmCriteria){
    		$scope.datasetSelected.algorithmCriteria = algorithmCriteria;
    	})
    }

	$scope.getPeersInNetwork = function(){
		$http.get('dataset/getPeersInNetwork').success(function(peersList){
			$scope.peersList = peersList;
		})
	}
	
	$scope.connect = function(){
		alert($scope.remoteUserIp + " connected")
		$http.post('dataset/connectTo/'+ $scope.remoteUserIp + '/' +'ip').success(function(id){
			$scope.getPeersInNetwork();
		})
	}
  
	/**
	 * Executing while reload
	 */

    $scope.fetchDatasetsInFolder;

    $scope.fetchDatasets();
	$scope.getAttributeTypeOptions();
	
	$scope.getPeersInNetwork();
	

    
});