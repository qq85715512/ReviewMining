package com.caiwm.reviewmining;

import java.util.Map;
import java.util.Set;

import com.caiwm.reviewmining.beans.FeatureOpinion;
import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;
import com.caiwm.reviewmining.core.Apriori;
import com.caiwm.reviewmining.core.AprioriParameters;
import com.caiwm.reviewmining.core.OpinionMine2;
import com.caiwm.reviewmining.core.RuleBasedFilter;
import com.caiwm.reviewmining.core.RuleBasedFilter2;
import com.caiwm.reviewmining.core.SentiAnalysis;

public class App {
	public static void main(String[] args) {
		
		//iphone6
		//mi4
		//samsungS6
		FileContant.setProduct("samsungS6");
		
		//利用Apriori算法初步挖掘特征，得到特征集F1
		AprioriParameters parameters = new AprioriParameters();
		parameters.setUp(0.007F);
		Apriori apriori = new Apriori(parameters);
		apriori.genFeature();
		
		//遍历F1中的每一个项，查找该项所在的所有句子；
		//如果该项在句子中的前后有形容词修饰，则可以确定为产品特征
		Set<Item> items = FileObjectProvider.readFile2ItemSet(FileContant.RESULT_FEATURE_FILE);
		RuleBasedFilter2.filterBySentiment(items);
		System.out.println("基于规则过滤后的特征集：" + RuleBasedFilter2.getFilteredFeature().size());
		System.out.println(RuleBasedFilter2.getFilteredFeature());
		System.out.println();
		Set<Item> feature = RuleBasedFilter2.getFilteredFeature();
		OpinionMine2.mineOpinion(feature);
		
		Map<String, Set<Integer>> featureOpinion = OpinionMine2.getFeatureOpnion();
		System.out.println(featureOpinion);
		System.out.println(featureOpinion.size());
		
		Set<FeatureOpinion> featureOpinions = SentiAnalysis.classify(featureOpinion);
		SentiAnalysis.calculate(featureOpinions);
	}
}
