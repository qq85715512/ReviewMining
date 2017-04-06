package com.caiwm.reviewmining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.beans.Word;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;

public class RuleBasedFilter {

	private static Set<Item> filteredFeature = new HashSet<Item>();

	public static Set<Item> getFilteredFeature() {
		return filteredFeature;
	}
	
	public static void filterBySentiment(Set<Item> items) {
		//List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);
		String wsFileName = FileContant.WS_SENTENCE_FILE;
		List<Sentence> sentences = FileObjectProvider.getSentencesFromFile(wsFileName);
		int threshole = 3;
		for (Item item : items) {
			int count = 0;
			String element = item.getElementsString();
			Set<Integer> sentenceNo = item.getSentenceNo();
			for (int no : sentenceNo) {
				Sentence sentence = sentences.get(no);
				List<Word> words = sentence.getWords();
				int i = sentence.findIndexOfWordContent(element);
				boolean left = searchLeft(i, words);
				boolean right = searchRight(i, words);
				if (left || right) {
					count++;
				}
			}
			if (count >= threshole) {
				filteredFeature.add(item);
			}
		}
		save2File();
	}

	private static boolean searchLeft(int i, List<Word> words) {
		int j = i - 1;
		while (j > 0 && words.get(j).getContent().equals("的"))
			j--;
		if (j >= 0 && j < i && words.get(j).getPos().equals("a")) {
			return true;
		}
		return false;
	}

	private static boolean searchRight(int i, List<Word> words) {
		int j = i + 1;
		while (j < words.size() - 1 && words.get(j).getPos().equals("d"))
			j++;
		if (j > i && j < words.size() && words.get(j).getPos().equals("a")) {
			return true;
		}
		return false;
	}

	private static void save2File() {
		String fileName = FileContant.RESULT_FILTER_FILE;
		Iterator<Item> iterator = filteredFeature.iterator();
		File result = new File(fileName);
		try {
			FileWriter fw = new FileWriter(result);
			while (iterator.hasNext()) {
				Item item = iterator.next();
				String element = item.getElementsString();
				String sentenceNo = item.getSentenceNoString();
				String line = element + "###" + sentenceNo + "\n";
				fw.write(line);
				fw.flush();
			}
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("没有找到此文件...");
		}
	}
}
