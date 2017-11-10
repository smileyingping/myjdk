package java.util;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class MyArraylist<T> implements Iterable<T> {

	private final static int INIT_COUNT = 10;

	private int size = 0;

	private transient int modcount;

	Object[] arrays;

	public MyArraylist() {
		this(INIT_COUNT);
	}

	public MyArraylist(int count) {
		arrays = new Object[count];
	}

	public void add(Object obj, int index) {
		checkIndexRangeForAdd(index);
		checkIndex(size + 1);
		System.arraycopy(arrays, index, arrays, index + 1, size - index);
		arrays[index] = obj;
		size++;
	}

	public boolean add(Object obj) {
		checkIndex(size + 1);
		modcount++;//加1
		arrays[size++] = obj;
		return true;
	}

	public void checkIndex(int index) {
		if (index > arrays.length) {
			modcount++;//加1
			int oldLength = size;
			int newLength = oldLength + (index >> 1);
			arrays = Arrays.copyOf(arrays, newLength);
		}
	}

	public T get(int index) {
		checkIndexRangeForAdd(index);
		return (T) arrays[index];
	}

	public T remove(int index) {
		checkIndexRange(index);
		modcount++;//加1

		@SuppressWarnings("unchecked")
		T t = (T) arrays[index];
		int moveLength = size - index - 1;
		if (moveLength > 0) {
			System.arraycopy(arrays, index + 1, arrays, index, moveLength);
		}
		arrays[--size] = null;
		return t;
	}

	public T set(Object obj, int index) {
		checkIndexRange(index);
		modcount++;//加1
		T t = (T) arrays[index];
		arrays[index] = obj;
		return t;
	}

	public void checkIndexRange(int index) {
		if (index > size) {
			throw new IndexOutOfBoundsException("数组越界");
		}
	}

	public void checkIndexRangeForAdd(int index) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("数组越界");
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public String toString() {
		Iterator<T> it = iterator();
		if (!it.hasNext())
			return "[]";

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			T e = it.next();
			sb.append(e == this ? "(this Collection)" : e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}

	public Iterator<T> iterator() {
		return new MyArrayListIterator();
	}

	private class MyArrayListIterator implements Iterator<T> {

		private int count;

		private int exceptCount = modcount;

		public boolean hasNext() {
			return count != size;
		}

		public T next() {
			checkModcount();
			return (T) arrays[count++];
		}

		public void checkModcount() {
			if (exceptCount != modcount) {
				throw new ConcurrentModificationException();
			}
		}

	}

}
