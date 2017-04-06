package com.caiwm.reviewmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.common.FileContant;

import java.util.Set;

public class PMI {
	// 语料库中的情感词
	private static Map<String, Set<Integer>> sentiWordsOfCorpus;
	
	// 褒义词
	private static Map<String, Set<Integer>> commendatories = null;
	// 贬义词
	private static Map<String, Set<Integer>> derogratories = null;
	
	// Hownet褒义词
	private static Set<String> hCommendatories = null;
	// Hownet贬义词
	private static Set<String> hDerogratories = null;
	
	// 语料库褒义词
	private static Set<String> cCommendatories = null;
	// 语料库贬义词
	private static Set<String> cDerogratories = null;

	private static Map<String, Set<Integer>> adjMap = null;
	
	private static Set<String> negative = new HashSet<String>();
	
	private static Set<String> positive = new HashSet<String>();
	
	public static Set<String> getNegative(){
		return negative;
	}
	
	public static Set<String> getPositive() {
		return positive;
	}
	
	static {
//		FileContant.setProduct("iphone6");
		sentiWordsOfCorpus = new HashMap<String, Set<Integer>>();
		String path = FileContant.SENTIMENT_WORDS_FILE;
		File sentiWords = new File(path);
		try {
			FileReader fr = new FileReader(sentiWords);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				Set<Integer> integers = new HashSet<Integer>();
				String[] strings = line.split("###");
				String sentiWord = strings[0];
				String noString = strings[1];
				noString = noString.substring(1, noString.length() - 1);
				String[] sententceNos = noString.split(",");
				for (String no : sententceNos) {
					integers.add(Integer.parseInt(no.trim()));
				}
				sentiWordsOfCorpus.put(sentiWord, integers);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//positivecomment
		String hPosPath = FileContant.POSITIVE_COMMENT_FILE;
		hCommendatories = getLexiconFromFile(hPosPath);
		
		//negativecomment
		String hNegPath = FileContant.NEGATIVE_COMMENT_FILE;
		hDerogratories = getLexiconFromFile(hNegPath);

		String cPosPath = FileContant.POSITICE_FILE;
		cCommendatories = getLexiconFromFile(cPosPath);
		
		String cNegPath = FileContant.NEGATIVE_FILE;
		cDerogratories = getLexiconFromFile(cNegPath);

		adjMap = new HashMap<String, Set<Integer>>();
		String adjMapPath = FileContant.ADJ_FILE;
		File adjMapFile = new File(adjMapPath);
		try {
			FileReader fr = new FileReader(adjMapFile);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				Set<Integer> integers = new HashSet<Integer>();
				String[] strings = line.split("###");
				String adj = strings[0];
				String noString = strings[1];
				noString = noString.substring(1, noString.length() - 1);
				String[] sententceNos = noString.split(",");
				for (String no : sententceNos) {
					integers.add(Integer.parseInt(no.trim()));
				}
				adjMap.put(adj, integers);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		union();
		classify();
		save2File();
	}

	
	private static void union() {
		Set<String> temp1 = new HashSet<String>();
		Set<String> temp2 = new HashSet<String>();
		commendatories = new HashMap<String, Set<Integer>>();
		derogratories = new HashMap<String, Set<Integer>>();
		
		temp1 = hCommendatories;
		temp2 = hDerogratories;
		temp1.addAll(cCommendatories);
		temp2.addAll(cDerogratories);
		Set<String> target = sentiWordsOfCorpus.keySet();
		temp1.retainAll(target);
		temp2.retainAll(target);
		
		Iterator<Entry<String, Set<Integer>>> iterator = sentiWordsOfCorpus.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Set<Integer>> entry = iterator.next();
			String key = entry.getKey();
			Set<Integer> value = entry.getValue();
			if (temp1.contains(key) && commendatories.size() < 60 && value.size() > 10) {
				commendatories.put(key, value);
			}else if (temp2.contains(key) && derogratories.size() < 60  && value.size() > 3) {
				derogratories.put(key, value);
			}
		}
	}
	
	private static void classify() {
		Iterator<Entry<String, Set<Integer>>> iterator = adjMap.entrySet().iterator();
		Set<String> goodKey = commendatories.keySet();
		Set<String> badKey = derogratories.keySet();
		while (iterator.hasNext()) {
			Entry<String, Set<Integer>> entry = iterator.next();
			String key = entry.getKey();
			Set<Integer> value = entry.getValue();
			if (goodKey.contains(key)) {
				positive.add(key);
			} else if (badKey.contains(key)) {
				negative.add(key);
			} 
			else {
				calculatePolar(key, value);
			}
		}
	}
	
	private static void calculatePolar(String key, Set<Integer> value) {
		int keyFreq = value.size();
		float pos = 0;
		float neg = 0;
		Iterator<Entry<String, Set<Integer>>> iterator1 = commendatories.entrySet().iterator();
		pos = calculate(keyFreq, value, iterator1);
		Iterator<Entry<String, Set<Integer>>> iterator2 = derogratories.entrySet().iterator();
		neg = calculate(keyFreq, value, iterator2);
		if (pos > neg) {
			positive.add(key);
		} else {
			negative.add(key);
		}
	}

	private static float calculate(int keyFreq, Set<Integer> value, Iterator<Entry<String, Set<Integer>>> iterator) {
		float result = 0;
		while (iterator.hasNext()) {
			Entry<String, Set<Integer>> entry = iterator.next();
			Set<Integer> value1 = entry.getValue();
			Set<Integer> value2 = new HashSet<Integer>(value1);
			value2.retainAll(value);
			if (!value2.isEmpty()) {
				int key1Freq = value1.size();
				int conOcurrFreq = value2.size();
				result += conOcurrFreq / (keyFreq * key1Freq * 1.0);
			}
		}
		return result;
	}

	private static Set<String> getLexiconFromFile (String pathName) {
		Set<String> result = new HashSet<String>();
		File positive = new File(pathName);
		try {
			FileReader fr = new FileReader(positive);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null) {
				result.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static void save2File() {
		String fileName = FileContant.RESULT_POLAR_FILE;
		File result = new File(fileName);
		try {
			FileWriter fw = new FileWriter(result);
			Iterator<String> positr = positive.iterator();
			Iterator<String> negitr = negative.iterator();
			fw.write("positive:"+ positive.size() +"个\n");
			while (positr.hasNext()) {
				String line = positr.next();
				fw.write(line);
				fw.write("\n");
				fw.flush();
			}
			fw.write("negative:" + negative.size() + "个\n");
			while (negitr.hasNext()) {
				String line = negitr.next();
				fw.write(line);
				fw.write("\n");
				fw.flush();
			}
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("没有找到此文件...");
		}
	}
	
	public static void main(String[] args) {
		System.out.println(PMI.sentiWordsOfCorpus);
		System.out.println(PMI.commendatories.size());
		System.out.println(PMI.derogratories.size());
		System.out.println();
		System.out.println(PMI.adjMap.size());
		System.out.println(PMI.positive);
		System.out.println(PMI.negative);
		System.out.println(PMI.positive.size());
		System.out.println(PMI.negative.size());
//		System.out.println(hCommendatories);
//		System.out.println(hDerogratories);
	}
}
