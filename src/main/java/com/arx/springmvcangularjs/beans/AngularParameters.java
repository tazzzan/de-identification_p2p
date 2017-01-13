package com.arx.springmvcangularjs.beans;

import com.arx.springmvcangularjs.beans.column.ColumnClient;

public class AngularParameters implements java.io.Serializable {
	private static final long serialVersionUID = -1125964321801437740L;
 
	private boolean hierarchyIsTrue = false;
	private boolean hierarchySubmitted = false;
	private boolean hierarchyIsAutomatic = true;
	private int  hierarchyTransformationMode = 1;
	
	private int generalizationMin = 0;
	private int generalizationMax = 0;
	private int generalizationBiggerThan = 0;
	private int generalizationMedian = 0;
	private int generalizationInterval = 0;
	
	private boolean      algorithmIsSensitive = false;
	private ColumnClient sensitiveColumn = null; 
	private boolean      algorithmIsSensitiveSubmitted = false;
	
	private boolean      algorithmIsLDiversity = false;
	private int          algorithmLDiversityValue = 0;
	private ColumnClient algorithmLDiversitySensitiveColumn = null;
	private int          algorithmLDiversityRecursiveC = 0;
	
	private boolean      algorithmIsTCloseness = false;
	private String       algorithmTClosenessPath = "";
	private int          algorithmTClosenessHierarchialGroundDistance = 0;
	
	
	public AngularParameters() {
		
	}
	
	public boolean isAlgorithmIsSensitiveSubmitted() {
		return algorithmIsSensitiveSubmitted;
	}
	public void setAlgorithmIsSensitiveSubmitted(boolean algorithmIsSensitiveSubmitted) {
		this.algorithmIsSensitiveSubmitted = algorithmIsSensitiveSubmitted;
	}

	public boolean isHierarchyIsTrue() {
		return hierarchyIsTrue;
	}
	public int getHierarchyTransformationMode() {
		return hierarchyTransformationMode;
	}




	public void setHierarchyTransformationMode(int hierarchyTransformationMode) {
		this.hierarchyTransformationMode = hierarchyTransformationMode;
	}




	public int getGeneralizationMin() {
		return generalizationMin;
	}




	public void setGeneralizationMin(int generalizationMin) {
		this.generalizationMin = generalizationMin;
	}




	public int getGeneralizationMax() {
		return generalizationMax;
	}




	public void setGeneralizationMax(int generalizationMax) {
		this.generalizationMax = generalizationMax;
	}




	public int getGeneralizationBiggerThan() {
		return generalizationBiggerThan;
	}




	public void setGeneralizationBiggerThan(int generalizationBiggerThan) {
		this.generalizationBiggerThan = generalizationBiggerThan;
	}




	public int getGeneralizationMedian() {
		return generalizationMedian;
	}




	public void setGeneralizationMedian(int generalizationMedian) {
		this.generalizationMedian = generalizationMedian;
	}




	public int getGeneralizationInterval() {
		return generalizationInterval;
	}




	public void setGeneralizationInterval(int generalizationInterval) {
		this.generalizationInterval = generalizationInterval;
	}




	public boolean isAlgorithmIsSensitive() {
		return algorithmIsSensitive;
	}




	public void setAlgorithmIsSensitive(boolean algorithmIsSensitive) {
		this.algorithmIsSensitive = algorithmIsSensitive;
	}




	public ColumnClient getSensitiveColumn() {
		return sensitiveColumn;
	}




	public void setSensitiveColumn(ColumnClient sensitiveColumn) {
		this.sensitiveColumn = sensitiveColumn;
	}




	public boolean getAlgorithmIsLDiversity() {
		return algorithmIsLDiversity;
	}




	public void setAlgorithmIsLDiversity(boolean algorithmIsLDiversity) {
		this.algorithmIsLDiversity = algorithmIsLDiversity;
	}




	public int getAlgorithmLDiversityValue() {
		return algorithmLDiversityValue;
	}




	public void setAlgorithmLDiversityValue(int algorithmLDiversityValue) {
		this.algorithmLDiversityValue = algorithmLDiversityValue;
	}




	public ColumnClient getAlgorithmLDiversitySensitiveColumn() {
		return algorithmLDiversitySensitiveColumn;
	}




	public void setAlgorithmLDiversitySensitiveColumn(ColumnClient algorithmLDiversitySensitiveColumn) {
		this.algorithmLDiversitySensitiveColumn = algorithmLDiversitySensitiveColumn;
	}




	public int getAlgorithmLDiversityRecursiveC() {
		return algorithmLDiversityRecursiveC;
	}




	public void setAlgorithmLDiversityRecursiveC(int algorithmLDiversityRecursiveC) {
		this.algorithmLDiversityRecursiveC = algorithmLDiversityRecursiveC;
	}




	public boolean isAlgorithmIsTCloseness() {
		return algorithmIsTCloseness;
	}




	public void setAlgorithmIsTCloseness(boolean algorithmIsTCloseness) {
		this.algorithmIsTCloseness = algorithmIsTCloseness;
	}




	public String getAlgorithmTClosenessPath() {
		return algorithmTClosenessPath;
	}




	public void setAlgorithmTClosenessPath(String algorithmTClosenessPath) {
		this.algorithmTClosenessPath = algorithmTClosenessPath;
	}




	public int getAlgorithmTClosenessHierarchialGroundDistance() {
		return algorithmTClosenessHierarchialGroundDistance;
	}




	public void setAlgorithmTClosenessHierarchialGroundDistance(int algorithmTClosenessHierarchialGroundDistance) {
		this.algorithmTClosenessHierarchialGroundDistance = algorithmTClosenessHierarchialGroundDistance;
	}




	public void setHierarchyIsTrue(boolean hierarchyIsTrue) {
		this.hierarchyIsTrue = hierarchyIsTrue;
	}
	public boolean isHierarchySubmitted() {
		return hierarchySubmitted;
	}
	public void setHierarchySubmitted(boolean hierarchySubmitted) {
		this.hierarchySubmitted = hierarchySubmitted;
	}
	public boolean isHierarchyIsAutomatic() {
		return hierarchyIsAutomatic;
	}
	public void setHierarchyIsAutomatic(boolean hierarchyIsAutomatic) {
		this.hierarchyIsAutomatic = hierarchyIsAutomatic;
	}
	
}
