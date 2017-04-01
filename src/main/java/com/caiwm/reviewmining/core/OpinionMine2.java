package com.caiwm.reviewmining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.beans.Word;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;

public class OpinionMine2 {

	private static Map<String, Set<Integer>> feaqtureOpinion = new HashMap<String, Set<Integer>>();

	private static Map<String, Set<Integer>> adjMap = new HashMap<String, Set<Integer>>();

	public static void mineOpinion(Set<Item> featureItems) {
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);
		for (Item item : featureItems) {
			String feature = item.getElementsString();
			Set<Integer> sentenceNo = item.getSentenceNo();
			for (int no : sentenceNo) {
				Sentence sentence = sentences.get(no);
				List<Word> words = sentence.getWords();
				int i = sentence.findIndexOfWordContent(feature);
				if (i > 0) {
					String adj = "";
					String adv = "";
					for (int j = i - 1; j > 0 && j > i - 5; j--) {
						Word word = words.get(j);
						if ("".equals(adj) && "a".equals(word.getPos())) {
							adj = word.getContent() + adj;
						} else if ("d".equals(word.getPos())) {
							adv = word.getContent() + "," + adv;
						}
					}
					if (!"".equals(adj)) {
						String fo = feature + "###" + adv + "###" + adj;
						if (adjMap.containsKey(adj)) {
							adjMap.get(adj).add(no);
						} else {
							Set<Integer> value = new HashSet<Integer>();
							value.add(no);
							adjMap.put(adj, value);
						}
						HashSet<Integer> hs = new HashSet<Integer>();
						hs.add(no);
						if (!feaqtureOpinion.containsKey(fo))
							feaqtureOpinion.put(fo, hs);
						else {
							feaqtureOpinion.get(fo).add(no);
						}
					}
				}
				if (i < words.size() - 1) {
					String adj = "";
					String adv = "";
					for (int j = i + 1; j < words.size() && j < i + 5; j++) {
						Word word = words.get(j);
						if ("".equals(adj) && "a".equals(word.getPos())) {
							adj = word.getContent() + adj;
						} else if ("d".equals(word.getPos())) {
							adv = word.getContent() + "," + adv;
						}
					}
					if (!"".equals(adj)) {
						String fo = feature + "###" + adv + "###" + adj;
						if (adjMap.containsKey(adj)) {
							adjMap.get(adj).add(no);
						} else {
							Set<Integer> value = new HashSet<Integer>();
							value.add(no);
							adjMap.put(adj, value);
						}
						HashSet<Integer> hs = new HashSet<Integer>();
						hs.add(no);
						if (!feaqtureOpinion.containsKey(fo))
							feaqtureOpinion.put(fo, hs);
						else {
							feaqtureOpinion.get(fo).add(no);
						}
					}
				}
			}
		}
		save2File();
		saveAdj2File();
	}

	private static void saveAdj2File() {
		String fileName = FileContant.ADJ_FILE;
		File file = new File(fileName);
		try {
			FileWriter fr = new FileWriter(file);
			Iterator<Entry<String, Set<Integer>>> iterator = adjMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Set<Integer>> entry = iterator.next();
				fr.write(entry.getKey() + "###" + entry.getValue());
				fr.write("\n");
				fr.flush();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Set<Integer>> getFeatureOpnion() {
		return feaqtureOpinion;
	}

	private static void save2File() {
		String fileName = FileContant.RESULT_FEATURE_OPINION_FILE;
		File file = new File(fileName);
		try {
			FileWriter fr = new FileWriter(file);
			for (Entry<String, Set<Integer>> entry : feaqtureOpinion.entrySet()) {
				fr.write(entry.getKey());
				fr.write(entry.getValue().toString());
				fr.write("\n");
				fr.flush();
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
