package com.caiwm.reviewmining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.beans.Word;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;

/**
 * 该类是Apriori算法的参数类：事务集合（每一个事务由名词构成）、最小支持度、最小置信度
 * 
 * @author caiwm
 *
 */
public class AprioriParameters {

	//事务数据库
	private Map<Integer, Set<String>> txDatabase;
	//最小支持度
	private Float minSup = 0.01F;
	//最小置信度
	private Float minConf = 0.1F;
	
	private Set<String> verbs;

	public AprioriParameters() {

	}

	public AprioriParameters(Float minSup, Float minConf) {
		this.minSup = minSup;
		this.minConf = minConf;
	}

	public void setUp(Float minSup) {
		this.minSup = minSup;
		buildTxDatabase(true);
	}

	public void setUp(String name, boolean isNew, Float minSup, Float minConf) {
		this.minSup = minSup;
		this.minConf = minConf;
		//构建事务数据库
		buildTxDatabase(isNew);
	}


	/**
	 * 构建事务数据库，扫描分词后的语料库
	 * 一条评论为一个事务，筛选出单条评论中的名词或名词性词语，形成一条事务
	 * @param name
	 * @param isNew
	 */
	private void buildTxDatabase(boolean isNew) {
		String wsFileName = FileContant.WS_FILE;
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(wsFileName, isNew);
		this.txDatabase = new HashMap<Integer, Set<String>>();
		//通过分析可以发现，产品特征的形式主要有一下几种：
		//名词，动名词，名词+名词，动词+名词
		verbs = new HashSet<String>();
		String[] str = { "n", "v", "vn", "nl", "ng" };
		List<String> nounFlag = Arrays.asList(str);
//		Set<String> sentimentWords = new HashSet<String>();
		Map<String, Set<Integer>> sentiWords = new TreeMap<String, Set<Integer>>();
		for (Sentence sentence : sentences) {
			List<Word> words = sentence.getWords();
			Set<String> items = new HashSet<String>();
			for (Word word : words) {
				if (nounFlag.contains(word.getPos()) && word.getContent().trim().length() > 1) {
					items.add(word.getContent());
					if ("v".equals(word.getPos())) {
						verbs.add(word.getContent());
					}
				} else if ("a".equals(word.getPos())) {
					String content = word.getContent();
					if (sentiWords.containsKey(content)) {
						sentiWords.get(content).add(sentence.getId());
					} else {
						Set<Integer> integers = new HashSet<Integer>();
						integers.add(sentence.getId());
						sentiWords.put(content, integers);
					}
//					sentimentWords.add(word.getContent());
				}
			}
			if (!items.isEmpty()) {
				this.txDatabase.put(sentence.getId(), items);
			}
		}
		saveSentiment2File(sentiWords);
	}

	private void saveSentiment2File(Map<String, Set<Integer>> sentiWords){
		String path = FileContant.SENTIMENT_WORDS_FILE;
		File file = new File(path);
		List<Entry<String, Set<Integer>>> arrayList = new ArrayList<Entry<String, Set<Integer>>>(sentiWords.entrySet());
		Collections.sort(arrayList, new Comparator<Entry<String, Set<Integer>>>() {
			public int compare(Entry<String, Set<Integer>> o1, Entry<String, Set<Integer>> o2) {
				return o2.getValue().size() - o1.getValue().size();
			}
		});
		try {
			FileWriter fr = new FileWriter(file);
			for (Entry<String, Set<Integer>> entry : arrayList) {
				fr.write(entry.getKey() + "###" + entry.getValue());
				fr.write("\n");
			}
			fr.close();
		} catch (IOException e) {
			throw new RuntimeException("没有找到此文件...");
		}
	}
	
	public Map<Integer, Set<String>> getTxDatabase() {
		return txDatabase;
	}

	public void setTxDatabase(Map<Integer, Set<String>> txDatabase) {
		this.txDatabase = txDatabase;
	}

	public Float getMinSup() {
		return minSup;
	}

	public void setMinSup(Float minSup) {
		this.minSup = minSup;
	}

	public Float getMinConf() {
		return minConf;
	}

	public void setMinConf(Float minConf) {
		this.minConf = minConf;
	}

	public Set<String> getVerbs() {
		return verbs;
	}

	public void setVerbs(Set<String> verbs) {
		this.verbs = verbs;
	}
}
