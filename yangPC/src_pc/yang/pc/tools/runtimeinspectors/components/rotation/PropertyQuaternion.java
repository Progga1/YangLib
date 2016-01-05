package yang.pc.tools.runtimeinspectors.components.rotation;

import yang.math.objects.EulerAngles;
import yang.math.objects.Quaternion;

public class PropertyQuaternion extends PropertyEulerAngles {

	private Quaternion mQuaternion;

	@Override
	protected void postInit() {
		super.postInit();
		if(mEulerAngles==null)
			mEulerAngles = new EulerAngles();
		if(!isReferenced()) {
			mQuaternion = new Quaternion();
		}
	}

	@Override
	protected void refreshInValue() {
		mEulerAngles.set(mQuaternion);
		super.refreshInValue();
	}

	@Override
	public void refreshOutValue() {
		super.refreshOutValue();
		mQuaternion.setFromEuler(mEulerAngles);
	}

	@Override
	public Object getValueReference() {
		return mQuaternion;
	}

	@Override
	public void setValueReference(Object reference) {
		mQuaternion = (Quaternion)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mQuaternion.set((Quaternion)value);
	}

}
