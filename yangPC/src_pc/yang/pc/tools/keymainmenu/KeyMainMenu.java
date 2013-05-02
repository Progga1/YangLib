package yang.pc.tools.keymainmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class KeyMainMenu implements ActionListener {

	private KeyMenuListener menuListener;
	private JMenuBar menuBar;
	private Stack<JMenu> subMenuStack;
	private JMenu curSubMenu;
	private HashMap<String,KeyItem> items;
	
	public KeyMainMenu(JMenuBar menuBar,KeyMenuListener menuListener) {
		if(menuBar==null)
			menuBar = new JMenuBar();
		this.menuBar = menuBar;
		this.menuListener = menuListener;
		items = new HashMap<String,KeyItem>(32);
		subMenuStack = new Stack<JMenu>();
		curSubMenu = null;
	}
	
	public KeyMainMenu(JFrame frame,KeyMenuListener menuListener) {
		this(new JMenuBar(),menuListener);
		frame.setJMenuBar(menuBar);
	}
	
	public void itemAdded(KeyItem item) {
		items.put(item.getKey(), item);
		item.addActionListener(this);
	}
	
	public KeyItem addItem(String key,String caption) {
		KeyItem newItem = new KeyItem(key,caption);
		itemAdded(newItem);
		curSubMenu.add(newItem);
		return newItem;
	}
	
	public KeyItem getItem(String key) {
		return items.get(key);
	}
	
	public JMenu createSubMenu(String title) {
		JMenu subMenu = new JMenu(title);
		if(subMenuStack.isEmpty()) {
			menuBar.add(subMenu);
		}else{
			subMenuStack.peek().add(subMenu);
		}
		subMenuStack.push(subMenu);
		curSubMenu = subMenu;
		return subMenu;
	}
	
	public JMenu nextSubMenu(String title) {
		popSubMenu();
		return createSubMenu(title);
	}
	
	public void addSeparator() {
		curSubMenu.addSeparator();
	}
	
	public void popSubMenu() {
		if(subMenuStack.isEmpty())
			return;
		subMenuStack.pop();
		if(subMenuStack.isEmpty())
			curSubMenu = null;
		else
			curSubMenu = subMenuStack.peek();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		menuListener.itemSelected(((KeyItem)ev.getSource()).getKey());
	}
	
}
