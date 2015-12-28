package yang.pc.tools.runtimeinspectors.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import yang.pc.tools.runtimeinspectors.InspectorComponent;

public class PropertyFloatNum extends InspectorComponent implements ActionListener {

	public NumTextField mNumTextField;
	public double mDefaultValue;

	public PropertyFloatNum(double defaultVal) {
		mDefaultValue = defaultVal;
	}

	public PropertyFloatNum() {
		this(0);
	}

	@Override
	protected void postInit() {
		mNumTextField = new NumTextField();
		mNumTextField.setActionListener(this);
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
		return mNumTextField.getFloat(0);
	}

	@Override
	public void setFloat(float value) {
		mNumTextField.setFloat(value);
	}

	@Override
	public double getDouble() {
		return mNumTextField.getDouble(0);
	}

	@Override
	public void setDouble(double value) {
		mNumTextField.setDouble(value);
	}

}
