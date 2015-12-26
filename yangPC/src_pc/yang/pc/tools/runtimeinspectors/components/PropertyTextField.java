package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public class PropertyTextField  extends InspectorComponent implements ActionListener {

	private JTextField mTextField;

	@Override
	protected void postInit() {
		mTextField = new JTextField();
		mTextField.addActionListener(this);
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

	@Override
	public void actionPerformed(ActionEvent ev) {
		setChanged();
	}

}
