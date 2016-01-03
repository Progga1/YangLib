package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.util.cameracontrol.Camera3DControl;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyEulerAngles;

public class PropertyCameraControl extends PropertyChain {

	private Camera3DControl mCamera;

	private PropertyEulerAngles mViewAngleComp;
	private PropertyVector3 mPositionComp;
	private PropertyFloatNum mDistanceComp;
	private PropertyNumArray mDelayComp;
	private PropertyBooleanCheckBox mInvertedComp;

	@Override
	protected InspectorComponent[] createComponents() {
		mViewAngleComp = new PropertyEulerAngles();
		mViewAngleComp.init(this,"Yaw pitch roll",true);

		mPositionComp = new PropertyVector3();
		mPositionComp.init(this,"Focus (xyz)",true);

		mDistanceComp = new PropertyFloatNum();
		mDistanceComp.init(this,"Distance",false);
		mDistanceComp.setMinValue(0.001f);

		mInvertedComp = new PropertyBooleanCheckBox();
		mInvertedComp.init(this,"Inside-out", false);

		mDelayComp = new PropertyNumArray(2);
		mDelayComp.init(this,"Delay (angle,zoom)",false);
		mDelayComp.setDefaultValue(1);
		mDelayComp.setClickSteps(0.1f);
		mDelayComp.setRange(0.001f,1);

		if(!isReferenced()) {
			setValueReference(new Camera3DControl());
		}

		return new InspectorComponent[]{mViewAngleComp,mPositionComp,mDistanceComp,mDelayComp,mInvertedComp};
	}

	@Override
	public void setValueReference(Object camera) {
		mCamera = (Camera3DControl)camera;
		mPositionComp.setValueReference(mCamera.mFocus);
		mViewAngleComp.setValueReference(mCamera.mTarViewValues);
	}

	@Override
	public void postValueChanged() {
		mDistanceComp.setFloat(mCamera.mTargetZoom);
		mDelayComp.setFloat(0,mCamera.mAngleDelay);
		mDelayComp.setFloat(1,mCamera.mZoomDelay);
		mInvertedComp.setBool(mCamera.mInvertView);
		super.postValueChanged();
	}

	@Override
	public void refreshOutValue() {
		super.refreshOutValue();
		mCamera.mTargetZoom = mDistanceComp.getFloat();
		mCamera.mAngleDelay = mDelayComp.getFloat(0);
		mCamera.mZoomDelay = mDelayComp.getFloat(1);
		mCamera.mInvertView = mInvertedComp.getBool();
		mCamera.snap();
	}

	@Override
	public Object getValueReference() {
		return mCamera;
	}

	@Override
	public void setValueFrom(Object value) {
		mCamera.set((Camera3DControl)value);
	}

}
