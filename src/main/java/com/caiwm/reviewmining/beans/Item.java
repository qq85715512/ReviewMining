package com.caiwm.reviewmining.beans;

import java.util.HashSet;
import java.util.Set;

/**
 * 频繁候选集或频繁项集的一个项，提供计算该项在事务数据库中的支持度
 * 
 * 频繁项集使用TreeSet数据结构（需要查找某个项），所以Item类需要实现Comparator接口
 * 
 * @author caiwm
 *
 */
public class Item implements Comparable<Item> {
	// 事务数据库的记录条数
	private Integer txDatabaseCount;
	// 项中的元素
	private Set<String> elements = new HashSet<String>();
	// 项所在的事务数据的编号，及评论编号
	private Set<Integer> sentenceNo = new HashSet<Integer>();

	public Item() {

	}

	public Item(int txDatabaseCount) {
		this.txDatabaseCount = txDatabaseCount;
	}

	// 当前项在事务数据库中出现的频次，及sentenceNo的大小
	public int getFrequent() {
		return sentenceNo.size();
	}

	// 计算该项在事务数据库的支持度
	public Float getSuport() {
		return new Float(getFrequent()) / new Float(txDatabaseCount);
	}

	public void addElement(String element) {
		this.elements.add(element);
	}
	
	public String getElementsString() {
		int size = this.elements.size();
		String[] elementArr = this.getElements().toArray(new String[size]);
		return array2String(size, elementArr);
	}

	public String getSentenceNoString() {
		int size = this.sentenceNo.size();
		Integer[] sentenceNoArr = this.sentenceNo.toArray(new Integer[size]);
		return array2String(size, sentenceNoArr);
	}

	private String array2String(int size, Object[] array) {
		String result = "";
		for (int i = 0; i < size; i++) {
			if (i == size - 1) {
				result += array[i];
			} else {
				result += array[i] + ", ";
			}
		}
		return result;
	}

	// *******************************************************************
	/**
	 * 方法hashcode、equals以及compareTo的功能是判断两个Item是否一样， 判断依据主要项中的所有元素（elements）是否一样
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elements == null) ? 0 : elements.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (elements == null) {
			if (other.elements != null)
				return false;
		} else if (!elements.equals(other.elements))
			return false;
		return true;
	}

	public int compareTo(Item o) {
		if (o.equals(this) && o.hashCode() == this.hashCode()) {
			return 0;
		}
		return this.hashCode() - o.hashCode() > 0 ? 1 : -1;
	}
	// *******************************************************************

	@Override
	public String toString() {
		return getElementsString();
	}

	public Integer getTxDatabaseCount() {
		return txDatabaseCount;
	}

	public void setTxDatabaseCount(Integer txDatabaseCount) {
		this.txDatabaseCount = txDatabaseCount;
	}

	public Set<String> getElements() {
		return elements;
	}

	public void setElements(Set<String> elements) {
		this.elements = elements;
	}

	public Set<Integer> getSentenceNo() {
		return sentenceNo;
	}

	public void setSentenceNo(Set<Integer> sentenceNo) {
		this.sentenceNo = sentenceNo;
	}
}