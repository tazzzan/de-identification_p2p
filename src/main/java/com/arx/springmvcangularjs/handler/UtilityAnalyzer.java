package com.arx.springmvcangularjs.handler;

import org.deidentifier.arx.ARXLattice.ARXNode;
import org.deidentifier.arx.ARXResult;

public class UtilityAnalyzer {

	public void getInfoInformationLoss(ARXResult result){
		ARXNode node = result.getGlobalOptimum();
		// Lower bound for the information loss
		System.out.println("min Inf Loss: " + node.getMinimumInformationLoss());
		System.out.println("max Inf Loss: " + node.getMaximumInformationLoss());
		System.out.println("total generalization" + node.getTotalGeneralizationLevel());
		System.out.println("anonymity: " +node.getAnonymity());
		System.out.println("predecessors: " + node.getPredecessors());
		System.out.println("successors: "+ node.getSuccessors());
		for(int i=0; i<node.getTransformation().length; i++){
			System.out.println("transformation " + i +": "+node.getTransformation()[i]);
		}
		
	}
	
	public String getInfoInformationLossValue(ARXResult result){
		ARXNode node = result.getGlobalOptimum();
		// Lower bound for the information loss
		return node.getMinimumInformationLoss().toString();
		
	}
}
