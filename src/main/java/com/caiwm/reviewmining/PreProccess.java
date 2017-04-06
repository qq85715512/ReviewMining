package com.caiwm.reviewmining;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import com.caiwm.reviewmining.common.FileContant;
import com.caiwm.reviewmining.nlpir.NlpirMethod;

public class PreProccess {
	
	private static final String express = "[\\.。,，\\?？!！;；]";
	
	private static void divide(String inPath, String outPath) {
		FileReader fr = null;
		FileWriter fw = null;
		try {
			fr = new FileReader(inPath);
			fw = new FileWriter(outPath);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while ((line = br.readLine()) != null && line.trim() != "") {
				String [] strs = line.split(express);
				for (String s : strs) {
					if (s != null && s.trim() != "") {
						fw.write(s);
						fw.write("\r\n");
					}
				}
			}
			br.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	
	public static void main(String[] args) {
		String[] products = new String[]{"iphone6", "mi4", "samsungS6"};
//		String[] products = new String[]{"iphone6"};
		String dictPath = "files/userdict.txt";
		int flag = NlpirMethod.NLPIR_ImportUserDict(dictPath, true);
		System.out.println(flag);
		
		for (String product : products) {
			FileContant.setProduct(product);
			String corpusPath = FileContant.CORPUS_FILE;
			String corpusSentencePath = FileContant.CORPUS_SENTENCE_FILE;
			String wdPath = FileContant.WS_FILE;
			String wdSentencePath =FileContant.WS_SENTENCE_FILE;
			divide(corpusPath, corpusSentencePath);
			NlpirMethod.NLPIR_FileProcess(corpusPath, wdPath, 1);
			NlpirMethod.NLPIR_FileProcess(corpusSentencePath, wdSentencePath, 1);
		}
	}
}
