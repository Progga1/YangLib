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

	protected double[] mDefaultVals;
	protected NumTextField[] mTextFields;

	public PropertyNumArrayBase(double... defaultVals) {
		mDefaultVals = defaultVals;
	}

	public PropertyNumArrayBase(int elemCount,double defaultVal) {
		mDefaultVals = new double[elemCount];
		for(int i=0;i<elemCount;i++) {
			mDefaultVals[i] = defaultVal;
		}
	}

	public PropertyNumArrayBase(int elemCount) {
		this(elemCount,0);
	}

	@Override
	protected void postInit() {
		mPanel = new JPanel();
		mPanel.setLayout(new GridLayout(1,3));
		mTextFields = new NumTextField[mDefaultVals.length];
		for(int i=0;i<mTextFields.length;i++) {
			mTextFields[i] = new NumTextField();
			mTextFields[i].setBorder(InspectorGUIDefinitions.COMPONENT_PADDING_BORDER);
			mPanel.add(mTextFields[i]);
			mTextFields[i].addActionListener(this);
		}
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
