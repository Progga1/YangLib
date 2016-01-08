package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.border.Border;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.interfaces.NameHolder;
import yang.pc.tools.runtimeinspectors.interfaces.NameInterface;
import yang.util.YangList;

public class PropertyComboBox extends InspectorComponent implements ActionListener {

	public static final int SHORTCUT_PREVIOUS = -1;
	public static final int SHORTCUT_NEXT = 1;

	protected JComboBox<String> mComboBox;
	protected YangList<NameInterface> mItems = new YangList<NameInterface>();
	protected static Border BORDER = BorderFactory.createEmptyBorder(4,4,4,4);

	protected String mOutputString = null;

	protected boolean mLockChange = false;

	public PropertyComboBox() {

	}

	public PropertyComboBox(NameInterface... items) {
		addItems(items);
	}

	public PropertyComboBox(String... items) {
		addItems(items);
	}

	@Override
	protected void postInit() {
		mComboBox = new JComboBox<String>();
		mComboBox.setBorder(BORDER);
		mComboBox.setPreferredSize(new Dimension(0,24));
		mComboBox.addActionListener(this);
		refreshLayout();
	}

	@Override
	public void loadFromStream(String value, BufferedReader reader) {
		setString(value);
	}

	public void refreshLayout() {
		if(mComboBox==null)
			return;
		mComboBox.removeAllItems();
		for(NameInterface item:mItems) {
			addToComboBox(item.getName());
		}
	}

	private boolean nameExists(String name) {
		int l = mComboBox.getItemCount();
		for(int i=0;i<l;i++) {
			if(name.equals(mComboBox.getItemAt(i)))
				return true;
		}
		return false;
	}

	private void addToComboBox(String name) {
		mLockChange = true;
		int i = 0;
		String origName = name;
		while(nameExists(name)) {
			i++;
			name = origName+"_"+i;
		}
		mComboBox.addItem(name);
		mLockChange = false;
	}

	public void addItem(NameInterface item) {
		mItems.add(item);
		if(mComboBox!=null) {
			addToComboBox(item.getName());
		}
	}

	public void addItem(String item) {
		addItem(new NameHolder(item));
	}

	public void addItems(NameInterface... items) {
		for(NameInterface item:items)
			addItem(item);
	}

	public void addItems(String... items) {
		for(String s:items)
			addItem(s);
	}

	@Override
	protected Component getComponent() {
		return mComboBox;
	}

	@Override
	public int getInt() {
		return mComboBox.getSelectedIndex();
	}

	@Override
	protected String getFileOutputString() {
		return mOutputString;
	}

	@Override
	public void setInt(int value) {
		mLockChange = true;
		int l = mComboBox.getItemCount()-1;
		if(l<0)
			return;
		if(value<0)
			value = 0;
		else if(value>l)
			value = l;
		mOutputString = mItems.get(value).getName();
		if(!isSaving()) {
			mComboBox.setSelectedIndex(value);
		}
		mLockChange = false;
	}

	@Override
	public String getString() {
		return (String)mComboBox.getSelectedItem();
	}

	@Override
	public void setString(String value) {
		mLockChange = true;
		int l = mComboBox.getItemCount();

		for(int i=0;i<l;i++) {
			if(value.equals(mComboBox.getItemAt(i))) {
				setInt(i);
				return;
			}
		}
		mLockChange = false;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!mLockChange)
			notifyValueUserInput();
	}

	@Override
	public boolean handleShortCut(int code) {
		if(code==0)
			code = SHORTCUT_NEXT;
		if(mComboBox.getItemCount()>0) {
			int newVal = getInt()+code;
			if(newVal<0)
				setInt(mComboBox.getItemCount()-1);
			else if(newVal>mComboBox.getItemCount()-1)
				setInt(0);
			else
				setInt(newVal);
		}
		return true;
	}

	@Override
	public void set(InspectorComponent template) {
		mItems = ((PropertyComboBox)template).mItems.clone();
		refreshLayout();
	}

}
