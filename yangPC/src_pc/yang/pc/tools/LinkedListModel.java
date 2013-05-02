package yang.pc.tools;

import java.util.LinkedList;

import javax.swing.AbstractListModel;

public class LinkedListModel<ElemType> extends AbstractListModel<ElemType>{

	private static final long serialVersionUID = 1L;
	private LinkedList<ElemType> list;
	
	public LinkedListModel(LinkedList<ElemType> list) {
		this.list = list;
	}
	
	public LinkedListModel() {
		this(new LinkedList<ElemType>());
	}
	
	public LinkedList<ElemType> getList() {
		return list;
	}

	@Override
	public ElemType getElementAt(int index) {
		return list.get(index);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	public void clear() {
		list.clear();
	}

	public void remove(int index) {
		list.remove(index);
	}

	@Override
	public String toString() {
		return list.toString();
	}
	
}
