package com.caiwm.reviewmining.beans;

public class FeatureOpinion {

	private String feature;
	
	private String escape;
	
	private String sentiWord;
	
	private int polar;
	
	private int count;
	
	public FeatureOpinion() {}

	public FeatureOpinion(String feature, String escape, String sentiWord, int polar, int count) {
		this.feature = feature;
		this.escape = escape;
		this.sentiWord = sentiWord;
		this.polar = polar;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "[feature=" + feature + ", escape=" + escape + ", sentiWord=" + sentiWord + ", polar="
				+ polar + ", count=" + count + "]";
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getEscape() {
		return escape;
	}

	public void setEscape(String escape) {
		this.escape = escape;
	}

	public String getSentiWord() {
		return sentiWord;
	}

	public void setSentiWord(String sentiWord) {
		this.sentiWord = sentiWord;
	}

	public int getPolar() {
		return polar;
	}

	public void setPolar(int polar) {
		this.polar = polar;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
