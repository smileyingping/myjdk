package jdk.util.myhashset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jdk.util.MyHashMap;
import jdk.util.MyHashSet;

public class Test {

	public static void main(String[] args) {
		Set<String> set = new HashSet<>();
		set.add("test");
		set.add("test2");

		for (String str : set) {

		}

		MyHashSet<String> set2 = new MyHashSet<>();
		set2.add("sdf");
		set2.add("hgf");

		for (String str : set2) {
			System.out.println("str = " + str);
		}

		Iterator it2 = set.iterator();
		while (it2.hasNext()) {
			String key = it2.next().toString();
			System.out.println("key = " + it2);
		}

		MyHashMap<String, String> map = new MyHashMap<>();
		map.put("sdf", "rtt");
		map.put("gdfg", "grt");

		Set<String> keys = map.keySet();
		System.out.println(keys);
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = map.get(key);
			System.out.println("key = " + key + " value = " + value);
		}

		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		System.out.println(entrySet);
		for (Map.Entry<String, String> entry : entrySet) {
			System.out.println("key = " + entry.getKey() + " value = " + entry.getValue());
		}

	}

}
