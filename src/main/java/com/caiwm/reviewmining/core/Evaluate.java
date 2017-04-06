package com.caiwm.reviewmining.core;

import java.util.HashSet;
import java.util.Set;

public class Evaluate {

	public static void evaluateFeatureMining(Set<String> result, Set<String> manual){
		Set<String> union = new HashSet<String>();
		for (String str : result) {
			union.add(str);
		}
		union.retainAll(manual);
		
		int unionCount = union.size();
		int manualCount = manual.size();
		int resultCount = result.size();
		
		double recall = unionCount / (manualCount * 1D);
		double precision = unionCount / (resultCount * 1D);
		double f = recall * precision * 2 / (recall + precision);
		System.out.println("recall:" + recall);
		System.out.println("precision:" + precision);
		System.out.println("f value:" + f);
	}
	
	public static void evaluateSentimentAnalysis(){
		
	}
}
