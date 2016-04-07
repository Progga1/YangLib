package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.intrinsics.CameraIntrinsicsFOV;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyCameraIntrinsicsFOV extends PropertyChain {

	protected CameraIntrinsicsFOV mIntrinsics;

	protected PropertyFloatNum mSkewProp;
	protected PropertyFieldOfView mFOVProp;
	protected PropertyPrincipalPoint mPrincipalPointProp;
	protected PropertyNumArray mNearFarProp;

	@Override
	protected InspectorComponent[] createComponents() {
		mFOVProp = new PropertyFieldOfView();
		mFOVProp.init(this,"Field of view",false);
		mPrincipalPointProp = new PropertyPrincipalPoint();
		mPrincipalPointProp.init(this,"Principal point",false);
		mSkewProp = new PropertyFloatNum();
		mSkewProp.init(this,"Skew",false);
		mSkewProp.setScrollFactor(0.1f);
		mNearFarProp = new PropertyNumArray(2);
		mNearFarProp.init(this,"Near, far",false);
		mNearFarProp.setScrollFactor(0.001f);
		mNearFarProp.setClickSteps(0.01f);
		mNearFarProp.setMinValue(0.0001f);

		return new InspectorComponent[]{mFOVProp,mNearFarProp,mPrincipalPointProp,mSkewProp};
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof CameraIntrinsicsFOV) {
			mIntrinsics = (CameraIntrinsicsFOV)reference;
		}else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mIntrinsics.mNear = mNearFarProp.getFloat(0);
		mIntrinsics.mFar = mNearFarProp.getFloat(1);
		mIntrinsics.setHalfFov(mFOVProp.getFOVXRad(),mFOVProp.getFOVYRad());
		mIntrinsics.setImageParameters(mPrincipalPointProp.getImageWidth(),mPrincipalPointProp.getImageHeight(),mPrincipalPointProp.getPrincipalPointX(),mPrincipalPointProp.getPrincipalPointY());
		mIntrinsics.setSkew(mSkewProp.getFloat());
		mIntrinsics.updateIntrinsicsMatByFOV();
	}

	@Override
	protected void refreshInValue() {
		mFOVProp.setFOVRad(mIntrinsics.getHalfFOVX(),mIntrinsics.getHalfFOVY());
		mPrincipalPointProp.setFloat(0,mIntrinsics.getImageWidth());
		mPrincipalPointProp.setFloat(1,mIntrinsics.getImageHeight());
		mPrincipalPointProp.setFloat(2,mIntrinsics.getPrincipalPointX());
		mPrincipalPointProp.setFloat(3,mIntrinsics.getPrincipalPointY());
		mNearFarProp.setFloat(0,mIntrinsics.mNear);
		mNearFarProp.setFloat(1,mIntrinsics.mFar);
		if(!mSkewProp.hasFocus())
			mSkewProp.setFloat(mIntrinsics.getSkew());
		super.refreshInValue();
	}

	@Override
	public PropertyCameraIntrinsicsFOV clone() {
		return new PropertyCameraIntrinsicsFOV();
	}

}
