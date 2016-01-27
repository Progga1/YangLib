package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.util.cameracontrol.Camera3DControl;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyBooleanCheckBox;
import yang.pc.tools.runtimeinspectors.components.PropertyVector3;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyFloatNum;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyEulerAngles;

public class PropertyCameraControl extends PropertyChain {

	private Camera3DControl mCamera;

	private PropertyEulerAngles mViewAngleComp;
	private PropertyVector3 mPositionComp;
	private PropertyFloatNum mDistanceComp;
	private PropertyBooleanCheckBox mOrthographicComp;
	private PropertyNumArray mDelayComp;
	private PropertyBooleanCheckBox mInvertedComp;

	@Override
	protected InspectorComponent[] createComponents() {
		mViewAngleComp = new PropertyEulerAngles();
		mViewAngleComp.init(this,"Yaw pitch roll",true);
		mViewAngleComp.setClampPitch(true);

		mPositionComp = new PropertyVector3();
		mPositionComp.init(this,"Focus (xyz)",true);

		mDistanceComp = new PropertyFloatNum();
		mDistanceComp.init(this,"Distance",false);
		mDistanceComp.setMinValue(0.001f);

		mInvertedComp = new PropertyBooleanCheckBox();
		mInvertedComp.init(this,"Inside-out",false);

		mOrthographicComp = new PropertyBooleanCheckBox();
		mOrthographicComp.init(this,"Orthographic",false);

		mDelayComp = new PropertyNumArray(2);
		mDelayComp.init(this,"Delay (angle,zoom)",false);
		mDelayComp.setDefaultValue(1);
		mDelayComp.setClickSteps(0.1f);
		mDelayComp.setRange(0.001f,1);

		if(!isReferenced()) {
			setValueReference(new Camera3DControl());
		}

		return new InspectorComponent[]{mViewAngleComp,mPositionComp,mDistanceComp,mOrthographicComp,mDelayComp,mInvertedComp};
	}

	@Override
	public void setValueReference(Object camera) {
		mCamera = (Camera3DControl)camera;
		mPositionComp.setValueReference(mCamera.mFocus);
		mViewAngleComp.setValueReference(mCamera.mTarViewValues);
	}

	@Override
	public void refreshInValue() {
		mDistanceComp.setFloat(mCamera.mTargetZoom);
		mOrthographicComp.setBool(mCamera.mOrthogonalProjection);
		mDelayComp.setFloat(0,mCamera.mAngleDelay);
		mDelayComp.setFloat(1,mCamera.mZoomDelay);
		mInvertedComp.setBool(mCamera.mInvertView);
		super.refreshInValue();
	}

	@Override
	public void refreshOutValue() {
		super.refreshOutValue();
		mCamera.mTargetZoom = mDistanceComp.getFloat();
		mCamera.mOrthogonalProjection = mOrthographicComp.getBool();
		mCamera.mAngleDelay = mDelayComp.getFloat(0);
		mCamera.mZoomDelay = mDelayComp.getFloat(1);
		mCamera.mInvertView = mInvertedComp.getBool();
		mCamera.snap();
	}

	public PropertyBooleanCheckBox getOrthographicProperty() {
		return mOrthographicComp;
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
