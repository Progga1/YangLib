package yang.pc.tools.runtimeinspectors.components.numbers;

import yang.pc.tools.runtimeinspectors.subcomponents.IntTextField;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyInteger extends PropertyNumBase {

	private IntTextField mIntTextField;

	@Override
	protected NumTextField createNumTextField() {
		mIntTextField = new IntTextField();
		return mIntTextField;
	}

	@Override
	public int getInt() {
		return mIntTextField.getInt();
	}

	@Override
	public void setInt(int value) {
		mIntTextField.setInt(value,!isSaving());
	}

	public PropertyInteger setMinValue(int minValue) {
		mNumTextField.setMinValue(minValue);
		return this;
	}

	public PropertyInteger setMaxValue(int maxValue) {
		mNumTextField.setMaxValue(maxValue);
		return this;
	}

	public PropertyInteger setRange(int minValue,int maxValue) {
		setMinValue(minValue);
		setMaxValue(maxValue);
		return this;
	}

}
