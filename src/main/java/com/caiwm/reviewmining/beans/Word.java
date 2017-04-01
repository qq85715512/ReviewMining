package com.caiwm.reviewmining.beans;

/**
 * 分词的封装类，表示评论中的每一个分词结果
 * 
 * @author caiwm
 *
 */
public class Word {

	//该分词在评论中的位置
	private Integer id;
	//该分词的具体内容
	private String content;
	//该分词的词性
	private String pos;

	public Word() {
	}

	public Word(int id, String content, String pos) {
		this.id = id;
		this.content = content;
		this.pos = pos;
	}

	@Override
	public String toString() {
		return content + "(" + pos + ")";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

}
