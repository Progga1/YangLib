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

	public void setInt(int value) {
		mIntTextField.setInt(value);
	}

}
