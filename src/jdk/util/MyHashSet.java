package jdk.util;

import java.util.Iterator;

public class MyHashSet<E> implements Iterable<E>{
	
	private transient MyHashMap<E, Object> map;
	
	private transient final Object value = new Object();
	
	public MyHashSet(){
		map = new MyHashMap<E, Object>();
	}
	
	public MyHashSet(int initCount){
		map = new MyHashMap<E, Object>(initCount);
	}
	
	public MyHashSet(int initCount, float loadFactor){
		map = new MyHashMap<E, Object>(initCount, loadFactor);
	}
	
	public Iterator<E> iterator(){
		return map.keySet().iterator();
	}
	
	public boolean add(E e){
		return map.put(e, value) == null;
	}
	
	public void remove(E e){
		map.remove(e);
	}

	@Override
	public String toString() {
		return map.toString();
	}

}
