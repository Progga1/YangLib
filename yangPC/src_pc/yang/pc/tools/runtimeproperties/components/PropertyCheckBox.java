package yang.pc.tools.runtimeproperties.components;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yang.pc.tools.runtimeproperties.InspectorGUIDefinitions;
import yang.pc.tools.runtimeproperties.RuntimePropertyComponent;

public class PropertyCheckBox extends RuntimePropertyComponent implements ChangeListener {

	private JCheckBox mCheckBox;

	@Override
	protected void postInit() {
		mCheckBox = new JCheckBox();
		mCheckBox.addChangeListener(this);
		mCheckBox.setBackground(InspectorGUIDefinitions.CL_VALUE_DEFAULT_BACKGROUND);
	}

	@Override
	protected void postSetValue(Object value) {
		mCheckBox.setSelected((Boolean)value);
	}

	@Override
	protected Object getValue() {
		return mCheckBox.isSelected();
	}

	@Override
	public Component getComponent() {
		return mCheckBox;
	}

	@Override
	public void stateChanged(ChangeEvent ev) {
		setChanged();
	}

}
