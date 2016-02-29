package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.intrinsics.CameraIntrinsics;
import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyCameraIntrinsics extends PropertyChain {

	private boolean mAdditionalFields;

	protected YangMatrix mIntrinsicsMatrix;
	protected CameraIntrinsics mIntrinsics;

	protected PropertyFloatNum mSkewProp;
	protected PropertyNumArray mPrincipalPointProp;
	protected PropertyNumArray mFocalLengthsProp;

	public PropertyCameraIntrinsics(boolean additionalFields) {
		mAdditionalFields = additionalFields;
	}

	public PropertyCameraIntrinsics() {
		this(true);
	}

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
		if(reference instanceof YangMatrix) {
			mIntrinsics = null;
			mIntrinsicsMatrix = (YangMatrix)reference;
		}else if(reference instanceof CameraIntrinsics) {
			mIntrinsics = (CameraIntrinsics)reference;
			mIntrinsicsMatrix = mIntrinsics.mIntrinsicsMatrix;
		}else
			super.setValueReference(reference);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		if(mIntrinsics!=null) {
			mIntrinsics.setFocalLength(mFocalLengthsProp.getFloat(0),mFocalLengthsProp.getFloat(1));
			mIntrinsics.setPrincipalPoint(mPrincipalPointProp.getFloat(0),mPrincipalPointProp.getFloat(1));
			mIntrinsics.setSkew(mSkewProp.getFloat());
		}else{
			mIntrinsicsMatrix.set(0,0,mFocalLengthsProp.getFloat(0));
			mIntrinsicsMatrix.set(1,1,mFocalLengthsProp.getFloat(1));
			mIntrinsicsMatrix.set(0,2,mPrincipalPointProp.getFloat(0));
			mIntrinsicsMatrix.set(1,2,mPrincipalPointProp.getFloat(1));
			mIntrinsicsMatrix.set(0,1,mSkewProp.getFloat());
		}

	}

	@Override
	protected void refreshInValue() {
		mFocalLengthsProp.setFloat(0,mIntrinsicsMatrix.get(0,0));
		mFocalLengthsProp.setFloat(1,mIntrinsicsMatrix.get(1,1));
		mPrincipalPointProp.setFloat(0,mIntrinsicsMatrix.get(0,2));
		mPrincipalPointProp.setFloat(1,mIntrinsicsMatrix.get(1,2));
		mSkewProp.setFloat(mIntrinsicsMatrix.get(0,1));
		super.refreshInValue();
	}

	@Override
	public PropertyCameraIntrinsics clone() {
		return new PropertyCameraIntrinsics(mAdditionalFields);
	}

}
