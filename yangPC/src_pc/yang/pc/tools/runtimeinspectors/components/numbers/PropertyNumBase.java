package yang.pc.tools.runtimeinspectors.components.numbers;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public abstract class PropertyNumBase extends InspectorComponent implements ActionListener{

	protected abstract NumTextField createNumTextField();

	public NumTextField mNumTextField;

	@Override
	protected void postInit() {
		mNumTextField = createNumTextField();
		mNumTextField.setBorder(InspectorGUIDefinitions.BORDER_COMPONENT_DEFAULT);
		mNumTextField.setActionListener(this);
	}

	public PropertyNumBase setScrollFactor(float stepsPerPixel) {
		mNumTextField.setScrollFactor(stepsPerPixel);
		return this;
	}

	@Override
	public void loadFromStream(String value,BufferedReader reader) {
		setDouble(Double.parseDouble(value));
	}

	@Override
	protected String getFileOutputString() {
		return mNumTextField.getValueString();
	}

	public void setCyclic(boolean cyclic) {
		mNumTextField.setCyclic(cyclic);
	}

	@Override
	public Component getComponent() {
		return mNumTextField;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		notifyValueUserInput();
	}

	@Override
	public void set(InspectorComponent template) {
		mNumTextField.copyParameters(((PropertyNumBase)template).mNumTextField);
	}

}
