package com.caiwm.reviewmining.beans;

import java.util.HashMap;
import java.util.Map;

public class OpinionCount {

	private Map<String, Integer> positive = new HashMap<String, Integer>();
	
	private Map<String, Integer> negative = new HashMap<String, Integer>();

	public Map<String, Integer> getPositive() {
		return positive;
	}

	public void setPositive(Map<String, Integer> positive) {
		this.positive = positive;
	}

	public Map<String, Integer> getNegative() {
		return negative;
	}

	public void setNegative(Map<String, Integer> negative) {
		this.negative = negative;
	}

	@Override
	public String toString() {
		return "[positive=" + positive + ", negative=" + negative + "]";
	}
	
	
}
