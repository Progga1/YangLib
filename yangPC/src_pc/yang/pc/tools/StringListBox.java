package yang.pc.tools;

import java.util.LinkedList;

import javax.swing.event.ListSelectionListener;

public class StringListBox extends LinkedListListBox<String> {

	private static final long serialVersionUID = 1L;

	public StringListBox(LinkedList<String> listReference, ListSelectionListener listener) {
		super(listReference, listener);
	}
	
	public StringListBox(ListSelectionListener listener) {
		this(new LinkedList<String>(),listener);
	}

		
}
