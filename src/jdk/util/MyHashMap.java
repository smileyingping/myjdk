package jdk.util;

import java.util.AbstractSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Iterator;

public class MyHashMap<K, V> {

	transient Node<K, V>[] table;
	
	private static int initCount = 16;//初始化长度
	
	private static float loadFator = 0.75f;//加载因子
	
	private int size;
	
	private static int threshold;//扩容临界值
	
	
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	public MyHashMap(){
		this(initCount, loadFator);
	}
	
	public MyHashMap(int initCount){
		this(initCount, loadFator);
	}
	
	public MyHashMap(int initCount, float loadFator){
		if(initCount < 0){
			throw new IllegalArgumentException("初始化长度不合法 : " + initCount);
		}
		
		if(initCount > MAXIMUM_CAPACITY){
			initCount = MAXIMUM_CAPACITY;
		}
		
		if(loadFator <0 || Float.isNaN(loadFator)){
			throw new IllegalArgumentException("加载因子不合法 : " + loadFator);
		}
		
		this.loadFator = loadFator;
		this.threshold = (int) (initCount * loadFator);
	}
	
	public Object put(K key, V value){
		if(table == null || table.length == 0){
			//此时真正创建数组
			table = new Node[initCount];
		}
		int hash = hash(key);
		int len = table.length;
		int index = (len-1) & hash;
		Node<K,V> p;
		if((p = table[index]) == null){
			table[index] = new Node<K, V>(hash, key, value, null);
		}else{//哈希碰撞
			MyHashMap<K, V>.Node<K, V> e = p;
			Node<K,V> temp = p;
			
			do{
				if(e.hash == hash && ((e.key == key) || (key != null && key.equals(e.key)))){
					V oldValue = p.value;
					//用新值覆盖旧值
					p.value = value;
					return oldValue;
				}
				temp = e;
			}while((e = temp.next) != null);
			//将元素放在链表最前面
			table[index] = new Node<K, V>(hash, key, value, p);
		}
		
		if(++size > threshold){//扩容
			resize();
		}
		
		return null;
	}
	
	public V get(K k){
		int hash = hash(k);
		int len = table.length;
		int index = (len-1) & hash;
		Node<K,V> node = table[index];//找到链表
		if (node == null) {
			return null;
		}
		Node<K,V> p;
		if ((p = node.next) == null) {//如果链表只有一个元素，直接返回
			return node.value;
		} else {//如果有多个元素，循环比较key
			p = node;
			do {
				if (p.key == k || p.key.equals(k)) {
					return p.value;
				}
				node = p;
			} while ((p = node.next) != null);
		}
		return null;
	}
	
	 
	public void remove(K k){
		int hash = hash(k);
		int len = table.length;
		int index = (len-1) & hash;
		
		Node<K,V> node = table[index];
		Node<K,V> prev = node;
		while (node != null) {
			if(node.hash == hash && (node.key == k || node.key.equals(k))){
				if(node == prev){//被删除元素在链表第一位
					table[index] = node.next;
				}else{
					prev.next = node.next;//node 为当前元素  prev为前一个元素  将前一个元素的指针next指向下一个元素
				}
				size--;
			}
			prev = node;
			node = node.next;
		};	
		
	}
	
	Set<K> keySet;
	
	public Set<K> keySet(){
		return keySet == null ? (keySet = new KeySet()) : null;
	}
	
	Set<Map.Entry<K,V>> entrySet;
	
	public Set<Map.Entry<K,V>> entrySet(){
		return entrySet == null ? (entrySet = new EntrySet()) : null;
	}
	
	class EntrySet extends AbstractSet<Map.Entry<K,V>>{

		@Override
		public Iterator<Map.Entry<K,V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return size;
		}
		
	}
	
	final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K,V>> {
		public final Node<K,V> next() {
			return nextNode();
		}
	}
	
	class KeySet extends AbstractSet<K>{

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public int size() {
			return size;
		}
		
	}
	
	final class KeyIterator extends HashIterator implements Iterator<K> {
		public final K next() {
			return nextNode().key;
		}
	}
	
	
	public class HashIterator{
		
		Node<K,V>[] nodes;
		Node<K,V> prve;
		Node<K,V> next;
		int index;
		
		public HashIterator(){//得到 keys.iterator();的时候执行此构造方法，找到第一个链表
			//找到第一个元素
			nodes = table;
			index = 0;
			prve = next = null;
			do {
				prve = next = table[index];
			} while((++index < table.length) && next == null);
		}
		
		final Node<K,V> nextNode(){
			Node<K,V> e = next;
			if((next = (prve = e).next) == null && nodes != null){//循环链表
				do {
				} while(index < table.length && (next = nodes[index++]) == null);//找到下一个链表
			}
			return e;
		}
		
		public boolean hasNext() {
			return next != null;
		}
	}	
	 
	 
  
	
	public void resize(){
		int len = size << 1;
		threshold = (int)(len * loadFator);
		size = 0;
		Node<K,V>[] newTable = new Node[len];
		Node<K,V>[] tempTable = table;
		table = newTable;
		
		int tempSize = tempTable.length;
		for(int i=0; i<tempSize; i++){
			Node<K,V> node = tempTable[i];
			while(node != null){
				put(node.key, node.value);
				node = node.next;
			}
		}
	}
	
	static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
	

	@SuppressWarnings("hiding")
	class Node<K, V> implements Map.Entry<K, V> {
		final int hash;

		final K key;
		V value;

		Node<K, V> next;

		Node(int hash, K key, V value, Node<K, V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V tempValue = value;
			this.value = value;
			return tempValue;
		}

		@Override
		public String toString() {
			return "[" + key + " : " + value + "]" + " next " + next;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof Map.Entry) {
				Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
				if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}

	}

}
