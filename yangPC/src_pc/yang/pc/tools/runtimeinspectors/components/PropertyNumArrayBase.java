package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;

public abstract class PropertyNumArrayBase extends InspectorComponent implements ActionListener {

	protected JPanel mPanel;

	protected NumTextField[] mTextFields;
	protected int mElemCount;

	public PropertyNumArrayBase(int elemCount) {
		mElemCount = elemCount;
	}

	@Override
	protected void postInit() {
		mPanel = new JPanel();
		mPanel.setLayout(new GridLayout(1,3));
		mTextFields = new NumTextField[mElemCount];
		for(int i=0;i<mTextFields.length;i++) {
			mTextFields[i] = new NumTextField();
			mTextFields[i].setBorder(InspectorGUIDefinitions.COMPONENT_PADDING_BORDER);
			mPanel.add(mTextFields[i]);
			mTextFields[i].setActionListener(this);
		}
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

	public void setMouseScrollFactor(float stepsPerPixel) {
		for(NumTextField textField:mTextFields) {
			textField.setMouseScrollFactor(stepsPerPixel);
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

}
