package com.caiwm.reviewmining.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.caiwm.reviewmining.beans.Item;
import com.caiwm.reviewmining.beans.Sentence;
import com.caiwm.reviewmining.beans.Word;
import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.common.FileObjectProvider;

public class Apriori {
	private Map<Integer, Set<String>> txDatabase; // 事务数据库
	private Float minSup; // 最小支持度
	// private Float minConf; // 最小置信度
	private Integer txDatabaseCount; // 事务数据库中的事务数
	private Set<String> verbs;

	TreeSet<Item> candidate1Item = new TreeSet<Item>();
	TreeSet<Item> candidate2Item = new TreeSet<Item>();
	TreeSet<Item> candidate3Item = new TreeSet<Item>();
	private Set<Item> feature = new TreeSet<Item>();

	public Apriori(AprioriParameters parameters) {
		this.txDatabase = parameters.getTxDatabase();
		this.minSup = parameters.getMinSup();
		// this.minConf = parameters.getMinConf();
		this.txDatabaseCount = this.txDatabase.size();
		feature = new TreeSet<Item>();
		verbs = parameters.getVerbs();
	}

	/**
	 * 计算候选特征集
	 */
	public void genFeature() {
		TreeSet<Item> freq1ItemSet = this.getFreq1ItemSet();
		Iterator<Item> it = freq1ItemSet.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			Set<String> elements = item.getElements();
			String element = elements.iterator().next();
			if (element.length() == 1) {
				it.remove();
			} else {
				candidate1Item.add(item);
			}
		}

		TreeSet<Item> freq2ItemSet = this.getFreqKItemSet(2, freq1ItemSet);
//		TreeSet<Item> freq3ItemSet = this.getFreqKItemSet(3, freq2ItemSet);

		candidate2Item = trim2ItemSet(freq2ItemSet);
//		candidate3Item = trim3ItemSet(freq3ItemSet);

		feature.addAll(candidate1Item);
		feature.addAll(candidate2Item);
//		feature.addAll(candidate3Item);
		System.out.println("候选1项集组成的特征：" + candidate1Item.size());
		System.out.println(candidate1Item);
		System.out.println();
		System.out.println("候选2项集组成的特征：" + candidate2Item.size());
		System.out.println(candidate2Item);
		System.out.println();
		System.out.println("整合候选1项集和候选2项集之后的特征集：" + feature.size());
		System.out.println(feature);
		System.out.println();
		filterByVerbs();
		System.out.println("动词过滤后的候选特征集：" + feature.size());
		System.out.println(feature);
		save2File();
	}

	private void filterByVerbs() {
		Iterator<Item> iterator = feature.iterator();
		while (iterator.hasNext()) {
			Item item = iterator.next();
			String element = item.getElementsString();
			if (verbs.contains(element)) {
				iterator.remove();
			}
		}
	}

	/**
	 * 扫描事务数据库，计算频繁1-项集
	 * 
	 * @return
	 */
	public TreeSet<Item> getFreq1ItemSet() {
		TreeSet<Item> freq1ItemSet = new TreeSet<Item>();
		TreeSet<Item> candFreq1ItemSet = this.getCandFreq1ItemSet();
		Iterator<Item> it = candFreq1ItemSet.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (item.getSuport() >= minSup) {
				freq1ItemSet.add(item);
			}
		}
		return freq1ItemSet;
	}

	/**
	 * 计算候选频繁1-项集
	 * 
	 * @return
	 */
	public TreeSet<Item> getCandFreq1ItemSet() {
		TreeSet<Item> candFreq1ItemSet = new TreeSet<Item>();
		Iterator<Map.Entry<Integer, Set<String>>> it = txDatabase.entrySet().iterator();
		// 统计支持数，生成候选频繁1-项集
		while (it.hasNext()) {
			Map.Entry<Integer, Set<String>> entry = it.next();
			Set<String> elementSet = entry.getValue();
			for (String element : elementSet) {
				Item item = new Item(txDatabaseCount);
				Set<String> elements = item.getElements();
				elements.add(element);
				if (!candFreq1ItemSet.contains(item)) {
					item.getSentenceNo().add(entry.getKey());
					candFreq1ItemSet.add(item);
				} else {
					candFreq1ItemSet.ceiling(item).getSentenceNo().add(entry.getKey());
				}
			}
		}
		return candFreq1ItemSet;
	}

	/**
	 * 根据频繁(k-1)-项集，调用aprioriGen方法，计算频繁k-项集
	 * 
	 * @param k
	 * @param freqMItemSet
	 *            频繁(k-1)-项集
	 * @return
	 */
	public TreeSet<Item> getFreqKItemSet(int k, TreeSet<Item> freqMItemSet) {
		// Map<Set<String>, Integer> candFreqKItemSetMap = new
		// HashMap<Set<String>, Integer>();
		TreeSet<Item> candFreqKItemSet = new TreeSet<Item>();
		// 调用aprioriGen方法，得到候选频繁k-项集
		Set<Item> candKItemSet = this.aprioriGen(k - 1, freqMItemSet);

		Iterator<Item> it = candKItemSet.iterator();
		while (it.hasNext()) {
			Item originItem = it.next();
			Item item = new Item(txDatabaseCount);
			Set<Integer> sentenceNo = originItem.getSentenceNo();
			for (Integer sentNo : sentenceNo) {
				Set<String> sentence = txDatabase.get(sentNo);
				if (sentence.containsAll(originItem.getElements())) {
					item.getElements().addAll(originItem.getElements());
					item.getSentenceNo().add(sentNo);
				}
			}
			if (!item.getElements().isEmpty()) {
				candFreqKItemSet.add(item);
			}
		}
		// 计算支持度，生成频繁k-项集，并返回
		return support(candFreqKItemSet);
	}

	/**
	 * 根据候选频繁k-项集，得到频繁k-项集
	 * 
	 * @param candFreqKItemSetMap
	 *            候选k项集(包含支持计数)
	 */
	private TreeSet<Item> support(TreeSet<Item> candFreqKItemSet) {
		TreeSet<Item> freqKItemSet = new TreeSet<Item>();
		Iterator<Item> it = candFreqKItemSet.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			// 计算支持度
			if (item.getSuport() < minSup) { // 如果不满足最小支持度，删除
				it.remove();
			} else {
				freqKItemSet.add(item);
			}
		}
		return freqKItemSet;
	}

	/**
	 * 根据频繁(k-1)-项集计算候选频繁k-项集
	 * 
	 * @param m
	 *            其中m=k-1
	 * @param freqMItemSet
	 *            频繁(k-1)-项集
	 * @return
	 */
	public TreeSet<Item> aprioriGen(int m, TreeSet<Item> freqMItemSet) {
		TreeSet<Item> candFreqKItemSet = new TreeSet<Item>();
		Iterator<Item> it = freqMItemSet.iterator();
		Item originalItemSet = null;
		while (it.hasNext()) {
			originalItemSet = it.next();
			Iterator<Item> itr = this.getIterator(originalItemSet, freqMItemSet);
			while (itr.hasNext()) {
				Set<String> identicalSet = new HashSet<String>(); // 两个项集相同元素的集合(集合的交运算)
				identicalSet.addAll(originalItemSet.getElements());
				Item set = itr.next();
				identicalSet.retainAll(set.getElements()); // identicalSet中剩下的元素是identicalSet与set集合中公有的元素
				if (identicalSet.size() == m - 1) { // (k-1)-项集中k-2个相同
					Set<String> differentSet = new HashSet<String>(); // 两个项集不同元素的集合(集合的差运算)
					differentSet.addAll(originalItemSet.getElements());
					differentSet.removeAll(set.getElements()); // 因为有k-2个相同，则differentSet中一定剩下一个元素，即differentSet大小为1
					differentSet.addAll(set.getElements()); // 构造候选k-项集的一个元素(set大小为k-1,differentSet大小为k)
					// 候选K项集
					Item item = new Item(txDatabaseCount);
					// 添加k项集的所有元素
					item.getElements().addAll(differentSet);
					// 添加k项集中所有元素出现的的句子的编号
					item.getSentenceNo().addAll(originalItemSet.getSentenceNo());
					item.getSentenceNo().addAll(set.getSentenceNo());
					candFreqKItemSet.add(item); // 加入候选k-项集集合
				}
			}
		}
		return candFreqKItemSet;
	}

	/**
	 * 根据一个频繁k-项集的元素(集合)，获取到频繁k-项集的从该元素开始的迭代器实例
	 * 
	 * @param itemSet
	 * @param freqKItemSet
	 *            频繁k-项集
	 * @return
	 */
	private Iterator<Item> getIterator(Item item, TreeSet<Item> freqMItemSet) {
		Iterator<Item> it = freqMItemSet.iterator();
		while (it.hasNext()) {
			if (item.equals(it.next())) {
				break;
			}
		}
		return it;
	}

	/**
	 * 根据邻近规则剪枝，处理2-项频繁项集
	 */
	public TreeSet<Item> trim2ItemSet(TreeSet<Item> freq2ItemSet) {
		TreeSet<Item> itemsAfterTrim = new TreeSet<Item>();
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);
		Iterator<Item> it = freq2ItemSet.iterator();
		while (it.hasNext()) {
			int positive = 0;

			Item item = it.next();
			Iterator<String> iterator = item.getElements().iterator();
			String[] elements = new String[2];
			int i = 0;
			while (iterator.hasNext()) {
				elements[i++] = iterator.next();
			}
			Set<Integer> sentenceNo = item.getSentenceNo();
			Set<Integer> newSentenceNo = new HashSet<Integer>();
			for (Integer no : sentenceNo) {
				int[] index = new int[2];
				Sentence sentence = sentences.get(no);
				index[0] = sentence.findIndexOfWordContent(elements[0]);
				index[1] = sentence.findIndexOfWordContent(elements[1]);

				if (index[0] >= 0 && index[1] >= 0 && Math.abs(index[1] - index[0]) <= 1) {
					newSentenceNo.add(no);
					positive += index[1] - index[0] > 0 ? 1 : -1;
				}
			}

			if (newSentenceNo.size() < sentenceNo.size() * 0.4) {
				continue;
			}
			String newElement = positive >= 0 ? (elements[0] + elements[1]) : (elements[1] + elements[0]);
			Item item2 = new Item(txDatabaseCount);
			item2.getElements().add(newElement);
			item2.getSentenceNo().addAll(newSentenceNo);
//			Float suport = item2.getSuport();
			//这里仍然采用支持度来过滤组合名词
//			if (suport > minSup) {
//				itemsAfterTrim.add(item2);
//				updateCandidate1Item(suport, elements, newSentenceNo);
//				updateSentences(newElement, elements, newSentenceNo, sentences);
//			}
			itemsAfterTrim.add(item2);
			updateCandidate1Item(elements, newSentenceNo);
			updateSentences(newElement, elements, newSentenceNo, sentences);
		}
		return itemsAfterTrim;
	}

	private void updateSentences(String newElement, String[] elements, Set<Integer> newSentenceNo, List<Sentence> sentences) {
		for (int no : newSentenceNo) {
			Sentence sentence = sentences.get(no);
			List<Word> words = sentence.getWords();
			int i = sentence.findIndexOfWordContent(elements[0]);
			int j = sentence.findIndexOfWordContent(elements[1]);
			if (i < j) {
				words.remove(j);
				words.remove(i);
				Word word = new Word(i, newElement, "n");
				words.add(i, word);
			} else {
				words.remove(i);
				words.remove(j);
				Word word = new Word(j, newElement, "n");
				words.add(j, word);
			}
		}
	}

	private void updateCandidate1Item(String[] elements, Set<Integer> newSentenceNo) {
		for (String string : elements) {
			Item item = new Item();
			item.getElements().add(string);
			Item item2 = candidate1Item.ceiling(item);
			item2.getSentenceNo().removeAll(newSentenceNo);
			if (item2.getSuport() < minSup) {
				candidate1Item.remove(item2);
			}
		}
	}

	/**
	 * 根据邻近规则剪枝，处理2-项频繁项集
	 */
	public TreeSet<Item> trim2ItemSet1(TreeSet<Item> freq2ItemSet) {
		TreeSet<Item> itemsAfterTrim = new TreeSet<Item>();
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);
		Iterator<Item> it = freq2ItemSet.iterator();
		while (it.hasNext()) {
			int positive = 0;

			Item item = it.next();
			Iterator<String> iterator = item.getElements().iterator();
			String[] elements = new String[2];
			int i = 0;
			while (iterator.hasNext()) {
				elements[i++] = iterator.next();
			}
			Set<Integer> sentenceNo = item.getSentenceNo();
			Set<Integer> newSentenceNo = new HashSet<Integer>();
			for (Integer no : sentenceNo) {
				Sentence sentence = sentences.get(no);
				int[] index = new int[2];
				index[0] = sentence.findIndexOfWordContent(elements[0]);
				index[1] = sentence.findIndexOfWordContent(elements[1]);

				if (index[0] >= 0 && index[1] >= 0 && Math.abs(index[1] - index[0]) <= 1) {
					newSentenceNo.add(no);
					positive += index[1] - index[0] > 0 ? 1 : -1;
				}
			}

			if (newSentenceNo.isEmpty()) {
				continue;
			}
			String newElement = positive >= 0 ? (elements[0] + elements[1]) : (elements[1] + elements[0]);
			Item item2 = new Item(txDatabaseCount);
			item2.getElements().add(newElement);
			item2.getSentenceNo().addAll(newSentenceNo);
			Float suport = item2.getSuport();
			if (suport > minSup) {
				itemsAfterTrim.add(item2);
				updateCandidate1Item(elements, newSentenceNo);
			}
		}
		return itemsAfterTrim;
	}
	
	/**
	 * 根据邻近规则剪枝，处理3-项频繁项集
	 */
	public TreeSet<Item> trim3ItemSet(TreeSet<Item> freq3ItemSet) {
		TreeSet<Item> itemsAfterTrim = new TreeSet<Item>();
		List<Sentence> sentences = FileObjectProvider.getDocumentsFromFile(null, false);
		Iterator<Item> it = freq3ItemSet.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			Set<String> elementSet = item.getElements();
			Iterator<String> iterator = elementSet.iterator();
			String[] elements = new String[3];
			int i = 0;
			while (iterator.hasNext()) {
				elements[i++] = iterator.next();
			}

			TreeMap<Integer, String> treeMap = new TreeMap<Integer, String>(new Comparator<Integer>() {

				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			Set<Integer> sentenceNo = item.getSentenceNo();
			Set<Integer> newSentenceNo = new HashSet<Integer>();
			for (Integer no : sentenceNo) {
				Sentence sentence = sentences.get(no);
				int[] index = new int[3];
				index[0] = sentence.findIndexOfWordContent(elements[0]);
				index[1] = sentence.findIndexOfWordContent(elements[1]);
				index[2] = sentence.findIndexOfWordContent(elements[2]);
				int distance1 = Math.abs(index[1] - index[0]);
				int distance2 = Math.abs(index[2] - index[1]);
				int distance3 = Math.abs(index[2] - index[0]);
				if (distance1 <= 3 && distance2 <= 3 && distance3 <= 3) {
					treeMap.put(index[0], elements[0]);
					treeMap.put(index[1], elements[1]);
					treeMap.put(index[2], elements[2]);
					newSentenceNo.add(no);
				}
			}

			if (treeMap.isEmpty()) {
				continue;
			}

			Iterator<String> iterator2 = treeMap.values().iterator();
			String newStr = "";
			while (iterator2.hasNext()) {
				newStr += iterator2.next();
			}
			Item item2 = new Item(txDatabaseCount);
			item2.getElements().add(newStr);
			item2.getSentenceNo().addAll(newSentenceNo);
			Float suport = item2.getSuport();
			if (suport > minSup) {
				itemsAfterTrim.add(item2);
				updateCandidate2Item(suport, elementSet, newSentenceNo);
			}
		}
		return itemsAfterTrim;
	}

	private void updateCandidate2Item(Float suport, Set<String> elements, Set<Integer> newSentenceNo) {
		for (String element : elements) {
			Item item = new Item();
			item.getElements().add(element);
			Item item2 = candidate2Item.ceiling(item);
			item2.getSentenceNo().removeAll(newSentenceNo);
			if (item2.getSuport() < minSup) {
				candidate2Item.remove(item2);
			}
		}
	}

	public Set<Item> getFeature() {
		return this.feature;
	}

	public void save2File() {
		Iterator<Item> iterator = this.feature.iterator();
		String fileName = FileContant.RESULT_FEATURE_FILE;
		File result = new File(fileName);
		try {
			FileWriter fw = new FileWriter(result);
			while (iterator.hasNext()) {
				Item item = iterator.next();
				String feature = item.getElementsString();
				String sentenceNo = item.getSentenceNoString();
				String line = feature + "###" + sentenceNo + "\n";
				fw.write(line);
				fw.flush();
			}
			fw.close();
		} catch (IOException e) {
			throw new RuntimeException("没有找到此文件...");
		}
	}
}
