package com.caiwm.reviewmining.common;

/**
 * 常量工具，用于生成文件路径：语料库文件、分词结果、特征挖掘结果
 * @author caiwm
 *
 */
public class Contant {

	public static final String TXT_SUFFIX = ".txt";

	public static final String CORPUS_PREFIX = "files/corpus/";

	public static final String CORPUS_SUFFIX = "_corpus.txt";

	public static final String WS_PREFIX = "files/ws/";

	public static final String WS_SUFFIX_WITH_POS = "_result_with_pos.txt";

	public static final String WS_SUFFIX_WITHOUT_POS = "_result_without_pos.txt";
	
	public static final String RESULT_PREFIX = "files/result/";
	
	public static final String RESULT_SUFFIX = "_result.txt";

	public static final String USER_DICT = "files/corpus/userDict.txt";

	public static final String COMMON_WORDS = "files/commonwords.txt";

	public static final String MANUAL_PREFIX = "files/manual/";

	public static final String MANUAL_SUFFIX = "_manual_feature.txt";

	public static final String STOP_WORDS = "files/lexicon/stopwords.txt";
	 
	public static final String ESCAPE = "files/lexicon/escape.txt";
	
	public static final String CONJUNCTIVE = "files/lexicon/conjunctive";
	
	public static final String DISJUNCTIVE = "files/lexicon/disjunctive";
	
	public static final String NEGATIVE = "files/lexicon/negative";
	
	public static final String POSITIVE = "files/lexicon/positives";

	/**
	 * 根据名称返回的文本文件相对路径，例如常用词文件、字典文件
	 */
	public static String getRawFile(String name) {
		return name + TXT_SUFFIX;
	}

	/**
	 * 根据产品名称返回语料库（未分词）的相对路径
	 */
	public static String getCorpusFile(String name) {
		return CORPUS_PREFIX + name + CORPUS_SUFFIX;
	}

	/**
	 * 根据产品名称返回语料库（已分词，含词性标注）的相对路径
	 */
	public static String getWsFileWithPos(String name) {
		return WS_PREFIX + name + WS_SUFFIX_WITH_POS;
	}
	
	/**
	 * 根据产品名称返回产品特征挖掘结果文件
	 */
	public static String getResultFile(String name) {
		return RESULT_PREFIX + name + RESULT_SUFFIX;
	}

	/**
	 * 根据产品名称返回语料库（已分词，不含词性标注）的相对路径
	 */
	public static String getWsFileWithoutPos(String name) {
		return WS_PREFIX + name + WS_SUFFIX_WITHOUT_POS;
	}

	/**
	 * 根据产品名称返回该产品的人工标注文件的相对路径
	 */
	public static String getManualFile(String name) {
		return MANUAL_PREFIX + name + MANUAL_SUFFIX;
	}
}
