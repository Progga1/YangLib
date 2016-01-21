package yang.pc.tools.runtimeinspectors.components.numbers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;

import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.LinkedNumComponents;
import yang.pc.tools.runtimeinspectors.subcomponents.CheckLabel;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public abstract class PropertyNumArrayBase extends InspectorComponent implements ActionListener {

	protected JPanel mPanel;

	protected LinkedNumComponents mLinks;
	protected CheckLabel mLinkCheckLabel = null;
	protected NumTextField[] mTextFields;
	protected int mElemCount;
	protected int mMaxColumns = Integer.MAX_VALUE;
	protected boolean mColumnMajor = false;

	public PropertyNumArrayBase(int elemCount) {
		mElemCount = elemCount;
	}

	@Override
	protected void postInit() {
		mPanel = new JPanel();
		int rows = mElemCount/mMaxColumns;
		if(rows<1)
			rows = 1;
		int columns = mMaxColumns;
		mPanel.setLayout(new GridLayout(rows,columns));
		mTextFields = new NumTextField[mElemCount];
		for(int i=0;i<mElemCount;i++) {
			mTextFields[i] = new NumTextField();
			mTextFields[i].setBorder(InspectorGUIDefinitions.BORDER_COMPONENT_DEFAULT);
			if(!mColumnMajor)
				mPanel.add(mTextFields[i]);
			mTextFields[i].setActionListener(this);
		}
		if(mColumnMajor) {
			for(int i=0;i<mElemCount;i++) {
				int row = i/columns;
				int col = i%columns;
				int newId = col*rows + row;
				if(newId<mElemCount)
					mPanel.add(mTextFields[newId]);
			}
		}
		mPanel.setPreferredSize(new Dimension(0,rows*InspectorGUIDefinitions.DEFAULT_COMPONENT_HEIGHT));
	}

	@Override
	protected String getFileOutputString() {
		String result = "";
		boolean first = true;
		for(NumTextField textField:mTextFields) {
			if(!first) {
				result += ",";
			}else
				first = false;
			result += textField.getValueString();
		}
		return result;
	}

	@Override
	protected void updateGUI() {
		for(NumTextField textField:mTextFields) {
			textField.updateGUI();
		}
	}

	@Override
	public void loadFromStream(String value,BufferedReader reader) {
		String[] split = value.split(",");
		int l = Math.min(mTextFields.length,split.length);
		for(int i=0;i<l;i++) {
			mTextFields[i].setByString(split[i]);
		}
	}

	@Override
	public void setLinkable() {
		if(mLinks!=null)
			return;
		mLinks = new LinkedNumComponents();
		for(NumTextField textField:mTextFields) {
			mLinks.addComponent(textField);
			textField.setLinkedNumberIOs(mLinks);
		}
		if(mHolder!=null)
			mHolder.setLinkable();
	}

	public void setDefaultValue(double defaultValue) {
		for(NumTextField textField:mTextFields) {
			textField.setDefaultValue(defaultValue);
		}
	}

	public void setMinValue(double minValue) {
		for(NumTextField textField:mTextFields) {
			textField.setMinValue(minValue);
		}
	}

	public void setMaxValue(double maxValue) {
		for(NumTextField textField:mTextFields) {
			textField.setMaxValue(maxValue);
		}
	}

	public void setMaxDigits(int maxDigits) {
		for(NumTextField textField:mTextFields) {
			textField.setMaxDigits(maxDigits);
		}
	}

	public void setScrollFactor(float stepsPerPixel) {
		for(NumTextField textField:mTextFields) {
			textField.setScrollFactor(stepsPerPixel);
		}
	}

	public void setClickSteps(float stepsPerClick) {
		for(NumTextField textField:mTextFields) {
			textField.setClickSteps(stepsPerClick);
		}
	}

	public void setRange(float minValue,float maxValue) {
		for(NumTextField textField:mTextFields) {
			textField.setRange(minValue,maxValue);
		}
	}

	public void setDefaultValue(int id,double defaultValue) {
		mTextFields[id].setDefaultValue(defaultValue);
	}

	public void setMinValue(int id,double minValue) {
		mTextFields[id].setMinValue(minValue);
	}

	public void setMaxValue(int id,double maxValue) {
		mTextFields[id].setMaxValue(maxValue);
	}

	public void setMaxDigits(int id,int maxDigits) {
		mTextFields[id].setMaxDigits(maxDigits);
	}

	@Override
	public Component getComponent() {
		return mPanel;
	}

	@Override
	public boolean hasFocus() {
		for(NumTextField textField:mTextFields) {
			if(textField.hasFocus())
				return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		notifyValueUserInput();
	}

	public void setFloat(int index,float value) {
		mTextFields[index].setFloat(value);
	}

	public float getFloat(int index) {
		return mTextFields[index].getFloat();
	}

	public void setDouble(int index,double value) {
		mTextFields[index].setDouble(value);
	}

	public double getDouble(int index) {
		return mTextFields[index].getDouble();
	}

	@Override
	public boolean isLinkingSupported() {
		return mLinks!=null;
	}

	@Override
	public void setLinkingActive(boolean active) {
		if(mLinks!=null) {
			mLinks.mLinkActive = active;
			if(mLinks.mLinkActive)
				mLinks.refreshStartValues();
			super.setLinkingActive(active);
		}
	}

	@Override
	public boolean isLinkingActive() {
		return mLinks!=null && mLinks.mLinkActive;
	}

	@Override
	protected void onSwitchObject() {
		if(isLinkingActive())
			mLinks.refreshStartValues();
	}

	@Override
	public void set(InspectorComponent template) {
		PropertyNumArrayBase conv = (PropertyNumArrayBase)template;
		for(int i=0;i<mTextFields.length;i++) {
			mTextFields[i].copyParameters(conv.mTextFields[i]);
		}
		if(conv.mLinks!=null)
			setLinkable();
	}

	@Override
	public boolean isReadOnly() {
		return mTextFields[0].isReadOnly();
	}

	@Override
	public PropertyNumArrayBase setReadOnly(boolean readOnly) {
		for(NumTextField textField:mTextFields) {
			textField.setReadOnly(readOnly);
		}
		return this;
	}

}
