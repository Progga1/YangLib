package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.util.cameracontrol.Camera3DControl;
import yang.math.objects.Vector3f;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;

public class PropertyCameraControl extends PropertyChain {

	private Camera3DControl mCamera;

	private PropertyVector3 mViewAngleComp;
	private PropertyVector3 mPositionComp;
	private PropertyFloatNum mDistanceComp;
	private PropertyNumArray mDelayComp;

	private Vector3f mAngleValues = new Vector3f();

	@Override
	protected InspectorComponent[] createComponents() {
		mViewAngleComp = new PropertyVector3();
		mViewAngleComp.init(this,"Yaw pitch roll",true);
		mViewAngleComp.setValueReference(mAngleValues);

		mPositionComp = new PropertyVector3();
		mPositionComp.init(this,"Focus (xyz)",true);

		mDistanceComp = new PropertyFloatNum();
		mDistanceComp.init(this,"Distance",false);
		mDistanceComp.setMinValue(0.001f);

		mDelayComp = new PropertyNumArray(2);
		mDelayComp.init(this,"Delay (angle,zoom)",false);
		mDelayComp.setDefaultValue(1);
		mDelayComp.setRange(0,1);

		if(!isReferenced()) {
			setValueReference(new Camera3DControl());
		}

		return new InspectorComponent[]{mViewAngleComp,mPositionComp,mDistanceComp,mDelayComp};
	}

	@Override
	public void setValueReference(Object camera) {
		mCamera = (Camera3DControl)camera;
		mPositionComp.setValueReference(mCamera.mFocus);
	}

	@Override
	public void postValueChanged() {
		mDistanceComp.setFloat(mCamera.mTargetZoom);
		mDelayComp.setFloat(0,mCamera.mAngleDelay);
		mDelayComp.setFloat(1,mCamera.mZoomDelay);
	}

	@Override
	public void refreshOutValue() {
		mCamera.mTargetZoom = mDistanceComp.getFloat();
		mCamera.mAngleDelay = mDelayComp.getFloat(0);
		mCamera.mZoomDelay = mDelayComp.getFloat(1);
	}

	@Override
	public Object getValueReference() {
		return mCamera;
	}

	@Override
	public void setValueFrom(Object value) {
		System.out.println("fnekf");
		mCamera.set((Camera3DControl)value);
	}

}
