package com.caiwm.reviewmining.beans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import junit.framework.TestCase;

public class ItemTest extends TestCase {

	@Test
	public void testItem(){
		TreeSet<Item> items = new TreeSet<Item>();
		Item item = new Item(1);
		Set<String> e = new HashSet<String>();
		e.add("a");
		item.getElements().add("a");
		Item item2 = new Item(1);
		item2.getElements().add("a");
		item2.getSentenceNo().add(1);
		items.add(item);
		// items.add(item2);
		items.ceiling(item2).getElements().add("b");
		System.out.println(items.contains(item2));
		System.out.println(items.size());
		System.out.println(items.ceiling(item).getElements());
		System.out.println();

		List<Item> items2 = new ArrayList<Item>();
		Item item1 = new Item(1);
		item1.getElements().add("b");
		Item item11 = new Item(1);
		item11.getElements().add("a");
		item11.getSentenceNo().add(1);
		// items2.add(item1);
		items2.add(item11);
		System.out.println(items2.contains(item11));
		System.out.println(items2);
		System.out.println(items2.indexOf(item1));
	}
}
