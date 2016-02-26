package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.intrinsics.CameraIntrinsics;
import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyCameraIntrinsics extends PropertyChain {

	protected YangMatrix mIntrinsics;

	protected PropertyFloatNum mSkewProp;
	protected PropertyNumArray mPrincipalPointProp;
	protected PropertyNumArray mFocalLengthsProp;

	@Override
	protected InspectorComponent[] createComponents() {
		mFocalLengthsProp = new PropertyNumArray(2);
		mFocalLengthsProp.init(this,"Focal lengths",false);
		mFocalLengthsProp.setLinkable();
		mPrincipalPointProp = new PropertyNumArray(2);
		mPrincipalPointProp.init(this,"Principal point", false);
		mSkewProp = new PropertyFloatNum();
		mSkewProp.init(this,"Skew",false);
		mSkewProp.setScrollFactor(0.1f);
		return new InspectorComponent[]{mFocalLengthsProp,mPrincipalPointProp,mSkewProp};
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof YangMatrix)
			mIntrinsics = (YangMatrix)reference;
		else if(reference instanceof CameraIntrinsics)
			mIntrinsics = ((CameraIntrinsics)reference).mIntrinsicsMatrix;
		else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mIntrinsics.set(0,0,mFocalLengthsProp.getFloat(0));
		mIntrinsics.set(1,1,mFocalLengthsProp.getFloat(1));
		mIntrinsics.set(0,2,mPrincipalPointProp.getFloat(0));
		mIntrinsics.set(1,2,mPrincipalPointProp.getFloat(1));
		mIntrinsics.set(0,1,mSkewProp.getFloat());
	}

	@Override
	protected void refreshInValue() {
		mFocalLengthsProp.setFloat(0,mIntrinsics.get(0,0));
		mFocalLengthsProp.setFloat(1,mIntrinsics.get(1,1));
		mPrincipalPointProp.setFloat(0,mIntrinsics.get(0,2));
		mPrincipalPointProp.setFloat(1,mIntrinsics.get(1,2));
		mSkewProp.setFloat(mIntrinsics.get(0,1));
		super.refreshInValue();
	}

	@Override
	public PropertyCameraIntrinsics clone() {
		return new PropertyCameraIntrinsics();
	}

}
