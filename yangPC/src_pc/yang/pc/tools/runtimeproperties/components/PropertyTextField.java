package yang.pc.tools.runtimeproperties.components;

import java.awt.Component;

import javax.swing.JTextField;

import yang.pc.tools.runtimeproperties.RuntimePropertyComponent;

public class PropertyTextField  extends RuntimePropertyComponent {

	private JTextField mTextField;

	@Override
	protected void postInit() {
		mTextField = new JTextField();
	}

	@Override
	protected void postSetValue(Object value) {
		mTextField.setText((String)value);
	}

	@Override
	protected Object getValue() {
		return mTextField.getText();
	}

	@Override
	public Component getComponent() {
		return mTextField;
	}

}
