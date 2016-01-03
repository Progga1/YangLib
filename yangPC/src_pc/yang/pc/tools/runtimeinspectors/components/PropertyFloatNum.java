package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.InspectorGUIDefinitions;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyFloatNum extends InspectorComponent implements ActionListener {

	public NumTextField mNumTextField;

	@Override
	protected void postInit() {
		mNumTextField = new NumTextField();
		mNumTextField.setBorder(InspectorGUIDefinitions.COMPONENT_PADDING_BORDER);
		mNumTextField.setActionListener(this);
	}

	public PropertyFloatNum setDefaultValue(float defaultValue) {
		mNumTextField.setDefaultValue(defaultValue);
		return this;
	}

	public PropertyFloatNum setMinValue(float minValue) {
		mNumTextField.setMinValue(minValue);
		return this;
	}

	public PropertyFloatNum setMaxValue(float maxValue) {
		mNumTextField.setMaxValue(maxValue);
		return this;
	}

	public PropertyFloatNum setMaxDigits(int maxDigits) {
		mNumTextField.setMaxDigits(maxDigits);
		return this;
	}

	public PropertyFloatNum setMouseScrollFactor(float stepsPerPixel) {
		mNumTextField.setScrollFactor(stepsPerPixel);
		return this;
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
	public float getFloat() {
		return mNumTextField.getFloat();
	}

	@Override
	public void setFloat(float value) {
		mNumTextField.setFloat(value);
	}

	@Override
	public double getDouble() {
		return mNumTextField.getDouble();
	}

	@Override
	public void setDouble(double value) {
		mNumTextField.setDouble(value);
	}

}
