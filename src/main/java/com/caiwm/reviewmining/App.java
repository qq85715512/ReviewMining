package com.caiwm.reviewmining;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.caiwm.reviewmining.beans.FeatureOpinion;
import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;
import com.caiwm.reviewmining.core.Apriori;
import com.caiwm.reviewmining.core.AprioriParameters;
import com.caiwm.reviewmining.core.Evaluate;
import com.caiwm.reviewmining.core.OpinionMine2;
import com.caiwm.reviewmining.core.RuleBasedFilter;
import com.caiwm.reviewmining.core.RuleBasedFilter2;
import com.caiwm.reviewmining.core.SentiAnalysis;

public class App {
	public static void main(String[] args) {
		
		//iphone6
		//mi4
		//samsungS6
		FileContant.setProduct("mi4");
		
		//利用Apriori算法初步挖掘特征，得到特征集F1
		AprioriParameters parameters = new AprioriParameters();
		parameters.setUp(0.006F);
		Apriori apriori = new Apriori(parameters);
		apriori.genFeature();
		
		//遍历F1中的每一个项，查找该项所在的所有句子；
		//如果该项在句子中的前后有形容词修饰，则可以确定为产品特征
		Set<Item> items = FileObjectProvider.readFile2ItemSet(FileContant.RESULT_FEATURE_FILE);
		transform2New(items);
		
		RuleBasedFilter.filterBySentiment(items);
		System.out.println("基于规则过滤后的特征集：" + RuleBasedFilter.getFilteredFeature().size());
		Set<Item> feature = RuleBasedFilter.getFilteredFeature();
		System.out.println(feature);
		
		Set<String> result = new HashSet<String>();
		for (Item item : feature) {
			result.add(item.getElementsString());
		}
		Set<String> manual = FileObjectProvider.getManual();
		Evaluate.evaluateFeatureMining(result, manual);
		
		OpinionMine2.mineOpinion(feature);
		
		Map<String, Set<Integer>> featureOpinion = OpinionMine2.getFeatureOpnion();
		System.out.println(featureOpinion);
		System.out.println(featureOpinion.size());
		createInputFile(featureOpinion);
		
		
		Set<FeatureOpinion> featureOpinions = SentiAnalysis.classify(featureOpinion);
		SentiAnalysis.calculate(featureOpinions);
	}
	
	private static void createInputFile(Map<String, Set<Integer>> featureOpinion){
		String inputFile = FileContant.RESULT_SIMRANK_INPUT_FILE;
		File file = new File(inputFile);
		try {
			FileWriter fw = new FileWriter(file);
			Iterator<String> iterator = featureOpinion.keySet().iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				String[] lineArr = line.split("###");
				String feature = lineArr[0];
				String sentiment = lineArr[2];
				fw.write(feature + "," + sentiment + "\n");
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void transform2New(Set<Item> items) {
		String wsFileName = FileContant.WS_SENTENCE_FILE;
		List<Sentence> sentences = FileObjectProvider.getSentencesFromFile(wsFileName);
		for (Item item : items) {
			String content = item.getElementsString();
			Set<Integer> sentenceNo = new HashSet<Integer>();
			for (Sentence sentence : sentences) {
				if (sentence.findIndexOfWordContent(content) != -1) {
					int no = sentence.getId();
					sentenceNo.add(no);
				}
			}
			item.setSentenceNo(sentenceNo);
		}
	}
}
