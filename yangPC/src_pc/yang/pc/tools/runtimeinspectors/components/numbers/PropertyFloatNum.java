package yang.pc.tools.runtimeinspectors.components.numbers;

import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyFloatNum extends PropertyNumBase {

	@Override
	protected NumTextField createNumTextField() {
		return new NumTextField();
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
