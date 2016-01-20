package yang.pc.tools.runtimeinspectors.components.camera;

import yang.graphics.camera.Camera3D;
import yang.math.objects.EulerAngles;
import yang.math.objects.Point3f;
import yang.math.objects.YangMatrix;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.PropertyVector3;
import yang.pc.tools.runtimeinspectors.components.rotation.PropertyEulerAngles;

//TODO not implemented

public class PropertyCamera extends PropertyChain {

	protected Point3f mPosition;
	protected EulerAngles mOrientation;

	protected PropertyVector3 mPositionComp;
	protected PropertyEulerAngles mEulerAnglesComp;

	protected Camera3D mCamera;

	public PropertyCamera() {
		super();
		throw new RuntimeException("Not implemented, yet");
	}

	@Override
	protected void postInit() {
		if(!isReferenced())
			mCamera = new Camera3D();
		super.postInit();
	}

	@Override
	protected InspectorComponent[] createComponents() {

		mPositionComp = new PropertyVector3();
		mPositionComp.init(this,"Position",true);
		mPositionComp.setValueReference(mPosition);

		mEulerAnglesComp = new PropertyEulerAngles();
		mEulerAnglesComp.init(this,"Orientation",true);
		mEulerAnglesComp.setFixedReference(mEulerAnglesComp);

		return null;
	}

	@Override
	public void setValueReference(Object camera) {
		mCamera = (Camera3D)camera;
	}

	@Override
	public void refreshInValue() {
		mCamera.getCameraTransformReference().getTranslation(mPosition);

		super.refreshInValue();
	}

	@Override
	public void refreshOutValue() {
		super.refreshOutValue();
		YangMatrix camTransf = mCamera.getCameraTransformReference();
		camTransf.loadIdentity();
		camTransf.translate(mPosition);
		camTransf.rotate(mOrientation);
	}

	@Override
	public Object getValueReference() {
		return mCamera;
	}

	@Override
	public void setValueFrom(Object value) {
		mCamera.set((Camera3D)value);
	}

}
