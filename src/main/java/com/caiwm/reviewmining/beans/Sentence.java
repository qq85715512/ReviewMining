package com.caiwm.reviewmining.beans;

import java.util.List;

/**
 * 评论封装类，这里规定一条用户评论就是一个句子
 * @id 该评论在语料库中的序号
 * @words 该评论中的所有词（句子的分词结果）
 * 
 * @author caiwm
 *
 */
public class Sentence {

	//评论编号
	private int id;
	//一条评论中的所有词语
	private List<Word> words;

	//根据词语定位该词语在评论中的位置
	public int findIndexOfWordContent(String content) {
		int index = -1;
		if (content == null) {
			return index;
		}
		for (int i = 0 ; i < this.words.size(); i++) {
				if (words.get(i).getContent().equals(content)) {
					index = i;
					break;
				}
		}
		return index;
	}
	
	public Sentence() {

	}

	@Override
	public String toString() {
		return words.toString();
	}

	public Sentence(int id) {
		this.id = id;
	}

	public int size() {
		return words.size();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}
}
