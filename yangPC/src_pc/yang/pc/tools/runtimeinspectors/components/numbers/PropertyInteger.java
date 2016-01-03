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

}
