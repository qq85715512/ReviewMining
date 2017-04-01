package com.caiwm.reviewmining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.caiwm.reviewmining.PMI;
import com.caiwm.reviewmining.beans.FeatureOpinion;
import com.caiwm.reviewmining.beans.OpinionCount;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;

public class SentiAnalysis {

	private static Set<String> positive = null;
	
	private static Set<String> escape = null;
	
	static {
		positive = PMI.getPositive();
//		negative = PMI.getNegative();
		escape = FileObjectProvider.getEscape();
	}
	
	private static Map<String, OpinionCount> map = null;
	
	public static Set<FeatureOpinion> classify(Map<String, Set<Integer>> feaqtureOpinion) {
		Set<FeatureOpinion> set = new HashSet<FeatureOpinion>();
		Iterator<Entry<String, Set<Integer>>> iterator = feaqtureOpinion.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Set<Integer>> entry = iterator.next();
			String key = entry.getKey();
			Set<Integer> nos = entry.getValue();
			int count = nos.size();
			String [] strs = key.split("###");
			String feature = strs[0];
			String adv = strs[1];
			String adj = strs[2];
			int polar = 0;
			if (positive.contains(adj)) {
				polar = 1;
			}
			if (!"".equals(adv) && escape.contains(adv)) {
				polar *= -1;
			} else {
				adv = "";
			}
			FeatureOpinion featureOpinion = new FeatureOpinion(feature, adv, adj, polar, count);
			set.add(featureOpinion);
		}
		return set;
	}
	
	public static void calculate(Set<FeatureOpinion> featureOpinions) {
		Set<FeatureOpinion> set = featureOpinions;
		map = new HashMap<String, OpinionCount>();
		Iterator<FeatureOpinion> iterator = set.iterator();
		while (iterator.hasNext()) {
			FeatureOpinion featureOpinion = iterator.next();
			String feature = featureOpinion.getFeature();
			if (map.containsKey(feature)) {
				OpinionCount opinionCount = map.get(feature);
				String sentiWord = featureOpinion.getSentiWord();
				int count = featureOpinion.getCount();
				if (featureOpinion.getPolar() == 0) {
					opinionCount.getNegative().put(sentiWord, count);
				} else {
					opinionCount.getPositive().put(sentiWord, count);
				}
				map.put(feature, opinionCount);
			} else {
				OpinionCount opinionCount = new OpinionCount();
				String sentiWord = featureOpinion.getSentiWord();
				int count = featureOpinion.getCount();
				if (featureOpinion.getPolar() == 0) {
					opinionCount.getNegative().put(sentiWord, count);
				} else {
					opinionCount.getPositive().put(sentiWord, count);
				}
				map.put(feature, opinionCount);
			}
		}
		saveResult2File();
	}

	private static void saveResult2File() {
		String path = FileContant.RESULT_FINAL_FILE;
		File file = new File(path);
		Iterator<Entry<String, OpinionCount>> iterator = map.entrySet().iterator();
		try {
			FileWriter fw = new FileWriter(file);
			String line = "";
			while (iterator.hasNext()) {
				Entry<String, OpinionCount> entry = iterator.next();
				String key = entry.getKey();
				OpinionCount opinionCount = entry.getValue();
				String posKey = getKeyString(opinionCount.getPositive());
				int posSum = getValuesSum(opinionCount.getPositive());
				String negKey = getKeyString(opinionCount.getNegative());
				int negSum = getValuesSum(opinionCount.getNegative());
				int sum = posSum + negSum;
				int posPer = (int) Math.round(posSum/(sum * 1.0) * 100);
				int negPer = (int) Math.round(negSum/(sum * 1.0) * 100);
				line = key + "###" + key + "###" + sum + "###" + posPer + "###" + negPer + "###" + posKey + "###" +negKey;
				fw.write(line);
				fw.write("\n");
				line = "";
			}
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("没有找到此文件...");
		}
	}

	private static String getKeyString(Map<String, Integer> map) {
		String keyStr = map.keySet().toString();
		return keyStr.substring(1, keyStr.length() - 1);
	}
	
	private static int getValuesSum(Map<String, Integer> map) {
		Collection<Integer> integers = map.values();
		int result = 0;
		for (int i : integers) {
			result += i;
		}
		return result;
	}
	
	public static Map<String, OpinionCount> getResult() {
		return map;
	}
	
	public static void main(String[] args) {
		String s = "东西######好";
		System.out.println(s.split("###")[1].equals(""));
	}
}
