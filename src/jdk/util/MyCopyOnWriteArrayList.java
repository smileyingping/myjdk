package jdk.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

public class MyCopyOnWriteArrayList<T> {

	final ReentrantLock lock = new ReentrantLock();

	private volatile Object[] array;	

	public Object[] getArray() {
		return array;
	}

	public void setArray(Object[] e) {
		this.array = e;
	}

	public MyCopyOnWriteArrayList() {
		setArray(new Object[0]);
	}

	public MyCopyOnWriteArrayList(Collection<? extends T> c) {
		if (c instanceof MyCopyOnWriteArrayList) {
			setArray(((MyCopyOnWriteArrayList) c).getArray());
		} else {
			setArray(c.toArray());
		}
	}
	
	public void add(T t){
		final ReentrantLock lock = this.lock;
		try {
			lock.lock();
			Object[] elements = getArray();
			int length = elements.length;
			Object[] newElements = Arrays.copyOf(elements, length + 1);
			newElements[length] = t;
			
			setArray(newElements);
		} finally {
			lock.unlock();
		}
	}
	
	public void add(T t, int index){
		final ReentrantLock lock = this.lock;
		try {
			lock.lock();
			Object[] elements = getArray();
			int len = elements.length;
			checkIndex(index, len);
			int numMove = len - index;
			Object[] newElements;
			if(numMove == 0){
				newElements = Arrays.copyOf(elements, len + 1);
				newElements[index] = t;
			}else{
				newElements = new Object[len + 1];
				System.arraycopy(elements, 0, newElements, 0, index);
				System.arraycopy(elements, index, newElements, index + 1, numMove);
				newElements[index] = t;
			}
			setArray(newElements);
		} finally {
			lock.unlock();
		}
	}
	
	public int addAll(Collection<? extends T> collections) {
		Object[] cs = collections.toArray();
		int len = cs.length;
		if (len == 0) {
			return 0;
		}
		final ReentrantLock lock = new ReentrantLock();
		try {
			lock.lock();
			Object[] elements = getArray();
			Object[] newElements = Arrays.copyOf(elements, elements.length + len);
			System.arraycopy(cs, 0, newElements, elements.length, len);
			setArray(newElements);
			return len;
		} finally {
			lock.unlock();
		}
	}
	
	public T get(int index){
		return (T) getArray()[index];
	}
	
	public T set(T t, int index){
		final ReentrantLock lock = new ReentrantLock();
		try {
			lock.lock();
			Object[] elements = getArray();
			checkIndex(index, elements.length);
			T oldValue = get(index);
			elements[index] = t;
			setArray(elements);
			return oldValue;
		} finally {
			lock.unlock();
		}
		
	}
	
	public T remove(int index){
		final ReentrantLock lock = new ReentrantLock();
		try {
			lock.lock();
			Object[] elements = getArray();
			int len = elements.length;
			checkIndex(index, len);
			T t = get(index);
			int moveLength = len - index - 1;
			if (moveLength > 0) {
				System.arraycopy(elements, index + 1, elements, index, moveLength);
			}
			elements[len - 1] = null;
			Object[] newElements = Arrays.copyOf(elements, len - 1);
			setArray(newElements);
			return t;
		} finally {
			lock.unlock();
		}
	}
	
	public void checkIndex(int index, int length){
		if(index <0 || index > length){
			throw new IndexOutOfBoundsException("数组越界");
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(array);
	}

}
