package yang.pc.tools.runtimeinspectors.components.camera;

import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyPrincipalPoint extends PropertyNumArray {

	public PropertyPrincipalPoint() {
		super(4);
	}

	@Override
	public void postInit() {
		super.postInit();
		setScrollFactor(0.2f);
		setMaxDigits(0,1);
		setMaxDigits(1,1);
		setMaxDigits(2,2);
		setMaxDigits(3,2);
		setMinValue(0,1);
		setMinValue(1,1);
	}

}
