package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.intrinsics.CameraIntrinsics;
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

	public float getImageWidth() {
		return getFloat(0);
	}

	public float getImageHeight() {
		return getFloat(1);
	}

	public float getPrincipalPointX() {
		return getFloat(2);
	}

	public float getPrincipalPointY() {
		return getFloat(3);
	}

	public void set(float imageWidth,float imageHeight,float principalX,float principalY) {
		setFloat(0,imageWidth);
		setFloat(1,imageHeight);
		setFloat(2,principalX);
		setFloat(3,principalY);
	}

	public void set(CameraIntrinsics intrinsics) {
		set(intrinsics.getImageWidth(),intrinsics.getImageHeight(),intrinsics.getPrincipalPointX(),intrinsics.getPrincipalPointY());
	}

}
