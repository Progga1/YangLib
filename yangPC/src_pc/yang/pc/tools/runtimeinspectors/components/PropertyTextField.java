package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;

public class PropertyTextField  extends InspectorComponent implements ActionListener {

	private JTextField mTextField;

	@Override
	protected void postInit() {
		mTextField = new JTextField();
		mTextField.addActionListener(this);
		//mTextField.getBorder()
		mTextField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(InspectorGUIDefinitions.CL_VALUE_DEFAULT_BACKGROUND, InspectorGUIDefinitions.COMPONENT_PADDING),InspectorGUIDefinitions.TEXT_FIELD_BORDER));
	}

	@Override
	public Component getComponent() {
		return mTextField;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		notifyValueUserInput();
	}

	@Override
	public String getString() {
		return mTextField.getText();
	}

	public void setString(String value) {
		mTextField.setText(value);
	}

}