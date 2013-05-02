package yang.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class NonConcurrentList<E> implements List<E> {

	private class Node {
		E elem;
		Node prev;
		Node next;
		
		Node(E e, Node p, Node n) {
			elem = e;
			prev = p;
			next = n;
		}
	}
	
	private class NonConcurrentIterator<T> implements ListIterator<T> {

		Node dummy = new Node(null,null,null);
		Node current;
		
		public boolean hasNext() {
			return current.next != null;
		}

		@SuppressWarnings("unchecked")
		public T next() {
			current = current.next;
			return (T) current.elem;
		}

		public void remove() {
			NonConcurrentList.this.remove((Node)current, (E)current.elem);
		}
		
		public void prepare(int idx) {
			current = (Node) first;
			for (int i = 0; i < idx; i++) current = current.next;
			
			dummy.next = current;
			current = dummy;
		}

		@SuppressWarnings("unchecked")
		public void add(T e) {
			NonConcurrentList.this.add((Node)current, (E)e, false);
		}

		public boolean hasPrevious() {
			return current.prev == null;
		}

		public int nextIndex() {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		public T previous() {
			current = current.prev;
			return (T) current.elem;
		}

		public int previousIndex() {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings("unchecked")
		public void set(T e) {
			current.elem = (E) e;
		}
	}
	
	private Node first;
	private Node last;
	private NonConcurrentIterator<E> iterator;
	private int size;
	
	public NonConcurrentList() {
		iterator = new NonConcurrentIterator<E>();
	}
	
	public boolean add(E element) {	//end of list
		add(last, element, true);
		return true;
	}

	public void add(int index, E element) {	//insert (before this item)
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("item: "+index +" size: "+size);
		}
		
		if (index == size) {
			add(element);	//end of list
		} else {
			Node curr = first;
			for(int i = 0; i < index; i++) curr = curr.next;
			add(curr, element, index==size);
		}		
	}

	public boolean addAll(Collection<? extends E> c) {
		for(E e : c) add(e);
		return true;
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		first = null;
		last = null;
		size = 0;
	}

	public boolean contains(Object o) {
		Node curr = first;
		while (curr != null) {
			if (curr.elem.equals(o)) return true;
			curr = curr.next;
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public E get(int index) {
		if (validIdx(index)) {
			Node curr = first;
			for (int i = 0; i < index; i++) {
				curr = curr.next;
			}
			return curr.elem;
		}
		return null;
	}

	public int indexOf(Object o) {
		int idx = -1;
		Node curr = first;
		while (curr != null) {
			idx++;
			if (curr.elem.equals(o)) return idx;
			curr = curr.next;
		}
		return idx;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Iterator<E> iterator() {
		iterator.prepare(0);
		return iterator;
	}

	public int lastIndexOf(Object o) {
		int idx = -1;
		int counter = -1;
		Node curr = first;
		while (curr != null) {
			counter++;
			if (curr.elem.equals(o)) idx = counter;
			curr = curr.next;
		}
		return idx;
	}

	public ListIterator<E> listIterator() {
		iterator.prepare(0);
		return iterator;
	}

	public ListIterator<E> listIterator(int index) {
		if (validIdx(index)) {
			iterator.prepare(index);
			return iterator;
		}
		return null;
	}

	public boolean remove(Object o) {
		Node curr = first;
		while (curr != null) {
			if (curr.elem.equals(o)) {
				remove(curr, curr.elem);
				return true;
			}
			curr = curr.next;
		}
		return false;
	}

	public E remove(int index) {
		if (validIdx(index)) {
			Node curr = first;
			for (int i = 0; i < index; i++) curr = curr.next;
			remove(curr, curr.elem);
			return curr.elem;
		}
		return null;
	}

	public E set(int index, E element) {
		if (validIdx(index)) {
			Node curr = first;
			for (int i = 0; i < index; i++) curr = curr.next;
			curr.elem = element;
		}
		return null;
	}

	public int size() {
		return size;
	}
	
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}	
	
	private boolean validIdx(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("item: "+index +" size: "+size);
		}
		return true;
	}
	
	public void add(Node node, E elem, boolean after) {
		size++;

		if (first == null) {
			first = new Node(elem, null, null);
			last = first;
		} else {
			if (after) {
				//add after node
				Node newNode = new Node(elem, node, node.next);
				if (node == last) last = newNode;
				node.next = newNode;
				if (newNode.next != null) newNode.next.prev = newNode;
				
			} else {
				//add before node
				Node newNode = new Node(elem, node.prev, node);
				if (node == first) first = newNode;
				
				node.prev = newNode;
				if (newNode.prev != null) newNode.prev.next = newNode;
			}
		}
	}
	
	public void remove(Node node, E elem) {
		size--;
		if (node == first) first = node.next;
		if (node == last) last = node.prev;
		
		Node prev = node.prev;
		Node next = node.next;
		
		if (prev != null) {
			prev.next = node.next;
		}
		
		if (next != null) {
			next.prev = node.prev;
		}
		
	}
	
	@Override
	public String toString() {
		String list = "[ | ";
		
		Node curr = first;
		while(curr != null) {
			list+= curr.elem.toString()+" | ";
			curr = curr.next;
		}
		
		return size + " items " + list+"] ";
	}
	
//	public static void main(String[] args) {
//		NonConcurrentList<String> list = new NonConcurrentList<String>();
//
//		String foo = "z";
//		
//		list.add("a");
//		list.add("b");
//		list.add("c");
//		list.add("d");
//		list.add("e");
//		list.add("f");
//		list.add("g");
//		list.add(foo);
//		list.add("h");
//		list.add("i");
//		list.add("j");
//		list.add("k");
//		
//		list.remove(8);
//		
//		System.out.println(list);
//	}
}
