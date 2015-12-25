package yang.pc.tools.runtimeproperties.components;

import java.awt.Component;

import javax.swing.JCheckBox;

import yang.pc.tools.runtimeproperties.RuntimePropertyComponent;

public class PropertyCheckBox extends RuntimePropertyComponent {

	private JCheckBox mCheckBox;

	@Override
	protected void postInit() {
		mCheckBox = new JCheckBox();
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
	protected void setEnabled(boolean enabled) {
		mCheckBox.setEnabled(enabled);
	}

}
