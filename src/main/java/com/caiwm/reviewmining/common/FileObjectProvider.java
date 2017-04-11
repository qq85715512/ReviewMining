package com.caiwm.reviewmining.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.beans.Word;

/**
 * 该类提供各种字典的集合对象：分词后的评论、转义副词、承接词、转折词、停用词、正面词汇、负面词汇
 * 
 * @author caiwm
 *
 */
public class FileObjectProvider {

	private static List<Sentence> documents = null;
	
	private static List<Sentence> sentences = null;

	private static Set<String> stop = null;

	private static Set<String> disjunctive = null;

	private static Set<String> conjunctive = null;

	private static Set<String> escape = null;

	private static Set<String> negative = null;

	private static Set<String> positive = null;
	
	private static Set<String> manual = null;
	
	private static Set<String> common_words = null;

	/**
	 * 加载分词后的评论文件，封装到Sentence列表
	 * @param wsFileName
	 * @param isNew
	 * @return
	 */
	public static List<Sentence> getDocumentsFromFile(String wsFileName, boolean isNew) {
		if (documents == null || isNew) {
			documents = readFile2List(wsFileName);
		}
		return documents;
	}

	public static List<Sentence> getSentencesFromFile(String wsFileName) {
		if (sentences == null) {
			sentences = readFile2List(wsFileName);
		}
		return sentences;
	}
	
	/**
	 * 按行读取文件，将内容封装到Sentence对象中，再放到列表中
	 * @param wsFileName
	 * @return
	 */
	private static List<Sentence> readFile2List(String wsFileName) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		FileReader fr = null;
		try {
			fr = new FileReader(wsFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int indexOfSentence = 0;
			while ((line = br.readLine()) != null && line.trim() != "") {
				Sentence sentence = new Sentence(indexOfSentence++);
				List<Word> words = new ArrayList<Word>();
				String[] strings = line.split(" ");
				int indexOfWord = 0;//分词在评论中的索引
				for (String string : strings) {
					if ("".equals(string.trim())) {
						continue;
					}
					try {
						//将所有字母都转成小写
						String content = string.split("/")[0].toLowerCase();
						String pos = string.split("/")[1];
						Word word = new Word(indexOfWord++, content, pos);
						words.add(word);
					} catch (ArrayIndexOutOfBoundsException e) {
						br.close();
						throw new RuntimeException("error is in: line" + indexOfSentence + ",cause by \"" + string + "\"");
					}
				}
				sentence.setWords(words);
				sentences.add(sentence);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sentences;
	}

	public static Set<String> getManual() {
		if (manual == null) {
			manual = readFile2Set(FileContant.MANUAL_FILE);
		}
		return manual;
	}
	
	/**
	 * 加载停用词
	 * @return
	 */
	public static Set<String> getStop() {
		if (stop == null) {
			stop = readFile2Set(Contant.STOP_WORDS);
		}
		return stop;
	}

	/**
	 * 加载转折词
	 * @return
	 */
	public static Set<String> getDisjunctive() {
		if (disjunctive == null) {
			disjunctive = readFile2Set(Contant.DISJUNCTIVE);
		}
		return disjunctive;
	}

	/**
	 * 加载承接词
	 * @return
	 */
	public static Set<String> getConjunctive() {
		if (conjunctive == null) {
			conjunctive = readFile2Set(Contant.CONJUNCTIVE);
		}
		return conjunctive;
	}

	/**
	 * 加载转义词
	 * @return
	 */
	public static Set<String> getEscape() {
		if (escape == null) {
			escape = readFile2Set(Contant.ESCAPE);
		}
		return escape;
	}

	/**
	 * 加载消极情感词
	 * @return
	 */
	public static Set<String> getNegative() {
		if (negative == null) {
			negative = readFile2Set(Contant.NEGATIVE);
		}
		return negative;
	}

	/**
	 * 加载积极情感词
	 * @return
	 */
	public static Set<String> getPositive() {
		if (positive == null) {
			positive = readFile2Set(Contant.POSITIVE);
		}
		return positive;
	}

	public static Set<String> getCommonWords() {
		if (common_words == null) {
			common_words = readFile2Set(FileContant.COMMON_WORDS);
		}
		return common_words;
	}
	
	/**
	 * 按行读取文件，将每行的内容加载到集合中
	 * @param fileName
	 * @return
	 */
	private static Set<String> readFile2Set(String fileName) {
		Set<String> set = new HashSet<String>();
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			while ((line = br.readLine()) != null && (line = line.trim()) != "") {
				set.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}
	
	public static Set<Item> readFile2ItemSet(String resultFile) {
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);

		Set<Item> items = new HashSet<Item>();
		try {
			FileReader fr = new FileReader(resultFile);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null && line.trim() != "") {
				Item item = record2Item(line);
				item.setTxDatabaseCount(sentences.size());
				items.add(item);
			}
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return items;
	}

	private static Item record2Item(String line) {
		String[] lineArr = line.split("###");
		String feature = lineArr[0];
		String[] sentenceNoArr = lineArr[1].split(",");
		Set<Integer> sentenceNo = new HashSet<Integer>();
		for (String no : sentenceNoArr) {
			if ((no = no.trim()) != "") {
				sentenceNo.add(Integer.parseInt(no));
			}
		}
		Item item = new Item();
		item.addElement(feature);
		item.getSentenceNo().addAll(sentenceNo);
		return item;
	}
}
