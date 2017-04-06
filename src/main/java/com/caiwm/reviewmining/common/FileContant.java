package com.caiwm.reviewmining.common;

public class FileContant {
	private static final String CORPUS_PREFIX = "files/corpus/";
	private static final String CORPUS_SUFFIX = ".txt";
	private static final String CORPUS_SENTENCE_SUFFIX = "_sentence.txt";
	
	private static final String WS_PREFIX = "files/ws/";
	private static final String WS_SUFFIX = "_with_pos.txt";
	private static final String WS_SENTENCE_SUFFIX = "_sentence_with_pos.txt";
	
	private static final String MANUAL_PREFIX = "files/manual/";
	private static final String MANUAL_SUFFIX = "_manual_feature.txt";
	
	private static final String SENTIMENT_PREFIX = "files/sentiment/";
	private static final String ADJ_SUFFIX = "_adj.txt";
	private static final String SENTIMENT_WORDS_SUFFIX = "_sentimentWords.txt";
	
	private static final String RESULT_PREFIX = "files/result/";
	private static final String RESULT_FEATURE_OPINION_SUFFIX = "_feature_opinion_result.txt";
	private static final String RESULT_FILTER_SUFFIX = "_filetr_result.txt";
	private static final String RESULT_FEATURE_SUFFIX = "_feature_result.txt";
	private static final String RESULT_FINAL_SUFFIX = "_result.txt";
	private static final String RESULT_POLAR_SUFFIX = "_polar_result.txt";
	private static final String RESULT_SIMRANK_INPUT_SUFFIX = "_simrank_input.txt";
	
	public static String CORPUS_FILE;
	public static String CORPUS_SENTENCE_FILE;
	public static String WS_FILE;
	public static String WS_SENTENCE_FILE;
	public static String MANUAL_FILE;
	public static String ADJ_FILE;
	public static String SENTIMENT_WORDS_FILE;
	public static String RESULT_FEATURE_FILE;
	public static String RESULT_FILTER_FILE;
	public static String RESULT_FEATURE_OPINION_FILE;
	public static String RESULT_FINAL_FILE;
	public static String RESULT_POLAR_FILE;
	public static String RESULT_SIMRANK_INPUT_FILE;
	public static String ESCAPE_FILE = "files/lexicon/escape.txt";
//	public static String POSITIVE_COMMENT_FILE = "files/lexicon/positivecomment.txt";
//	public static String NEGATIVE_COMMENT_FILE = "files/lexicon/negativecomment.txt";
	public static String POSITIVE_COMMENT_FILE = "files/lexicon/p.txt";
	public static String NEGATIVE_COMMENT_FILE = "files/lexicon/n.txt";
	public static String POSITICE_FILE = "files/lexicon/positive.txt";
	public static String NEGATIVE_FILE = "files/lexicon/negative.txt";
	
	public static void setProduct(String product) {
		CORPUS_FILE = CORPUS_PREFIX + product + CORPUS_SUFFIX;
		CORPUS_SENTENCE_FILE = CORPUS_PREFIX + product + CORPUS_SENTENCE_SUFFIX;
		WS_FILE = WS_PREFIX + product + WS_SUFFIX;
		WS_SENTENCE_FILE = WS_PREFIX + product + WS_SENTENCE_SUFFIX;
		MANUAL_FILE = MANUAL_PREFIX + product + MANUAL_SUFFIX;
		ADJ_FILE = SENTIMENT_PREFIX + product + ADJ_SUFFIX;
		SENTIMENT_WORDS_FILE = SENTIMENT_PREFIX + product + SENTIMENT_WORDS_SUFFIX;
		RESULT_FEATURE_OPINION_FILE = RESULT_PREFIX + product + RESULT_FEATURE_OPINION_SUFFIX;
		RESULT_FILTER_FILE = RESULT_PREFIX + product + RESULT_FILTER_SUFFIX;
		RESULT_FEATURE_FILE = RESULT_PREFIX + product + RESULT_FEATURE_SUFFIX;
		RESULT_FINAL_FILE = RESULT_PREFIX + product + RESULT_FINAL_SUFFIX;
		RESULT_POLAR_FILE = RESULT_PREFIX + product + RESULT_POLAR_SUFFIX;
		RESULT_SIMRANK_INPUT_FILE = RESULT_PREFIX + product +RESULT_SIMRANK_INPUT_SUFFIX;
	}
}
