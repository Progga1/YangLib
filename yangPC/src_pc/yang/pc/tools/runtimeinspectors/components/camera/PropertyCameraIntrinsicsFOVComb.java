package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.intrinsics.CameraIntrinsicsFOV;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyMatrix;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;

public class PropertyCameraIntrinsicsFOVComb extends PropertyChain {

	protected CameraIntrinsicsFOV mIntrinsics;

	protected PropertyFloatNum mSkewProp;
	protected PropertyFieldOfView mFOVProp;
	protected PropertyPrincipalPoint mPrincipalPointProp;
	protected PropertyMatrix mIntrinsicsMatrixProp;

	@Override
	protected InspectorComponent[] createComponents() {
		mFOVProp = new PropertyFieldOfView();
		mFOVProp.init(this,"Field of view",false);
		mPrincipalPointProp = new PropertyPrincipalPoint();
		mPrincipalPointProp.init(this,"Principal point",false);
		mSkewProp = new PropertyFloatNum();
		mSkewProp.init(this,"Skew",false);
		mSkewProp.setScrollFactor(0.1f);
		mIntrinsicsMatrixProp = new PropertyMatrix(3,3,4);
		mIntrinsicsMatrixProp.init(this,"Matrix",true);
		mIndividualFocus = true;
		return new InspectorComponent[]{mFOVProp,mPrincipalPointProp,mSkewProp,mIntrinsicsMatrixProp};
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof CameraIntrinsicsFOV) {
			mIntrinsics = (CameraIntrinsicsFOV)reference;
			mIntrinsicsMatrixProp.setValueReference(mIntrinsics.mIntrinsicsMatrix);
		}else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		mIntrinsicsMatrixProp.refreshOutValue();
		mIntrinsics.setFov(mFOVProp.getFOVXRad(),mFOVProp.getFOVYRad());
		mIntrinsics.setImageParameters(mPrincipalPointProp.getImageWidth(),mPrincipalPointProp.getImageHeight(),mPrincipalPointProp.getPrincipalPointX(),mPrincipalPointProp.getPrincipalPointY());
		mIntrinsics.setSkew(mSkewProp.getFloat());
	}

	protected void updateIntrinsicsMat() {
		mIntrinsicsMatrixProp.refreshInValue();
	}

	protected void updateFOV() {
		mFOVProp.setFOVRad(mIntrinsics.getHalfFOVX(),mIntrinsics.getHalfFOVY());
	}

	protected void updatePrincipalPoint() {
		mPrincipalPointProp.setFloat(0,mIntrinsics.getImageWidth());
		mPrincipalPointProp.setFloat(1,mIntrinsics.getImageHeight());
		mPrincipalPointProp.setFloat(2,mIntrinsics.getPrincipalPointX());
		mPrincipalPointProp.setFloat(3,mIntrinsics.getPrincipalPointY());
	}

	@Override
	protected void childValueChanged(InspectorComponent sender) {
		super.childValueChanged(sender);
		if(sender==mIntrinsicsMatrixProp) {
			mIntrinsicsMatrixProp.refreshOutValue();
			mIntrinsics.updateFOVByIntrinsicsMat();
			updateFOV();
			updatePrincipalPoint();
		}else if(sender==mFOVProp) {
			mIntrinsics.setFov(mFOVProp.getFOVXRad(),mFOVProp.getFOVYRad());
			updateIntrinsicsMat();
		}else if(sender==mPrincipalPointProp) {
			mIntrinsics.setImageParameters(mPrincipalPointProp.getImageWidth(),mPrincipalPointProp.getImageHeight(),mPrincipalPointProp.getPrincipalPointX(),mPrincipalPointProp.getPrincipalPointY());
			updateIntrinsicsMat();
		}else if(sender==mSkewProp) {
			mIntrinsics.setSkew(mSkewProp.getFloat());
			updateIntrinsicsMat();
		}
	}

	@Override
	protected void refreshInValue() {
		if(!mFOVProp.hasFocus())
			updateFOV();
		if(!mPrincipalPointProp.hasFocus()) {
			updatePrincipalPoint();
		}
		if(!mSkewProp.hasFocus())
			mSkewProp.setFloat(mIntrinsics.getSkew());
		super.refreshInValue();
	}

	@Override
	public PropertyCameraIntrinsicsFOVComb clone() {
		return new PropertyCameraIntrinsicsFOVComb();
	}

}
