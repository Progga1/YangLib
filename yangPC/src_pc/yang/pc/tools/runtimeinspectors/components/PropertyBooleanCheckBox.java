package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.io.BufferedReader;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;

public class PropertyBooleanCheckBox extends InspectorComponent implements ChangeListener {

	private JCheckBox mCheckBox;
	private boolean mChecked = false;

	@Override
	protected void postInit() {
		mCheckBox = new JCheckBox();
		mCheckBox.addChangeListener(this);
		mCheckBox.setBackground(InspectorGUIDefinitions.CL_COMPONENT_DEFAULT_BACKGROUND);
	}

	@Override
	public void setBool(boolean checked) {
		mChecked = checked;
	}

//	@Override
//	protected void refreshOutValue() {
//		mChecked = mCheckBox.isSelected();
//	}

	@Override
	protected String getFileOutputString() {
		return Boolean.toString(mChecked);
	}

	@Override
	protected void updateGUI() {
		if(mChecked!=mCheckBox.isSelected())
			mCheckBox.setSelected(mChecked);
	}

	@Override
	public void loadFromStream(String value, BufferedReader reader) {
		setBool(value.equals("true"));
	}

	@Override
	public boolean getBool() {
		return mChecked;
	}

	@Override
	public Component getComponent() {
		return mCheckBox;
	}

	@Override
	public void stateChanged(ChangeEvent ev) {
		mChecked = mCheckBox.isSelected();
		notifyValueUserInput();
	}

	@Override
	protected InspectorComponent handleShortCut(int code) {
		setBool(!getBool());
		return this;
	}

}
