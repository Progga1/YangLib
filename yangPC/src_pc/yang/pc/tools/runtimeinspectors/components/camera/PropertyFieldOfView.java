package yang.pc.tools.runtimeinspectors.components.camera;

import yang.math.MathConst;
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

	public void setFOVRad(float fovx, float fovy) {
		setFloat(0,fovx*MathConst.TO_DEG);
		setFloat(1,fovy*MathConst.TO_DEG);
	}

	public float getFOVXRad() {
		return getFloat(0)*MathConst.TO_RAD;
	}

	public float getFOVYRad() {
		return getFloat(1)*MathConst.TO_RAD;
	}

}
