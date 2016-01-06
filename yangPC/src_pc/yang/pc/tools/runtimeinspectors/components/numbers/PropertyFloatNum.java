package yang.pc.tools.runtimeinspectors.components.numbers;

import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyFloatNum extends PropertyNumBase {

//	protected FloatInterface mFloat;
//	protected DoubleInterface mDouble;

	@Override
	protected NumTextField createNumTextField() {
		return new NumTextField();
	}

//	@Override
//	public void setPreferredOutputType(Class<?> type) {
//		if(type==Double.class) {
//			mDoubleMode = true;
//		}
//	}

	public PropertyFloatNum setDefaultValue(double defaultValue) {
		mNumTextField.setDefaultValue(defaultValue);
		return this;
	}

	public PropertyFloatNum setMinValue(double minValue) {
		mNumTextField.setMinValue(minValue);
		return this;
	}

	public PropertyFloatNum setMaxValue(double maxValue) {
		mNumTextField.setMaxValue(maxValue);
		return this;
	}

	public PropertyFloatNum setRange(double minValue,double maxValue) {
		setMinValue(minValue);
		setMaxValue(maxValue);
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
		mNumTextField.setFloat(value,!isSaving());
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
