package yang.pc.tools.runtimeinspectors.components.camera;

import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyFieldOfView extends PropertyNumArray {

	public PropertyFieldOfView() {
		super(2);
	}

	@Override
	public void postInit() {
		super.postInit();
		setMinValue(0.1f);
		setLinkable();
		setLinkingActive(true);
	}

}
