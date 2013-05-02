package yang.pc.tools;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionListener;

public class LinkedListListBox<ElemType> extends JList<ElemType>{

	public static Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.black);
	
	private static final long serialVersionUID = 1L;
	private DefaultListModel<ElemType> listModel;
	private JScrollPane mScrollPane;
	
	public LinkedListListBox(LinkedList<ElemType> listReference,ListSelectionListener listener) {
		super();
		mScrollPane = new JScrollPane(this);
		mScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mScrollPane.setBorder(DEFAULT_BORDER);
		listModel = new DefaultListModel<ElemType>();
		//this.setModel(new LinkedListModel<ElemType>(listReference));
		this.setModel(listModel);
		this.addListSelectionListener(listener);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setVisibleRowCount(-1);
	}
	
	public void addItem(ElemType item) {
		//listReference.add(item);
		listModel.addElement(item);
	}
	
	public JScrollPane getScrollPane() {
		return mScrollPane;
	}
	
	public void clear() {
		//listReference.clear();
		listModel.clear();
	}
	
}
