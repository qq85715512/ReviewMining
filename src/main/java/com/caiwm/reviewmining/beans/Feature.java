package com.caiwm.reviewmining.beans;

import java.util.Set;

public class Feature {

	private String feature;
	
	private Set<String> sentenceNo;

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public Set<String> getSentenceNo() {
		return sentenceNo;
	}

	public void setSentenceNo(Set<String> sentenceNo) {
		this.sentenceNo = sentenceNo;
	}
	
}
