package com.caiwm.reviewmining;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class Test {

	private static Set<String> set = null;;
	
	public static Set<String> getSet() {
		if (set == null) {
			set = new HashSet<String>();
		}
		return set;
	}
	
	public static void main(String[] args) {
//		Set<String> s1 = Test.getSet();
//		s1.add("1");
//		Set<String> s2 = Test.getSet();
//		System.out.println(s2.size());
		List<Integer> integers = new ArrayList<Integer>();
		integers.add(1);
		integers.add(2);
		integers.add(3);
		integers.remove(2);
		integers.remove(1);
		integers.add(1, 4);
		System.out.println(integers);
	}
}
