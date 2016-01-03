package yang.pc.tools.runtimeinspectors.components;

import yang.math.MathConst;
import yang.math.objects.Quaternion;

public class PropertyQuaternion extends PropertyNumArrayBase {

	private Quaternion mQuaternion;

	public PropertyQuaternion() {
		super(3);
	}

	@Override
	protected void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mQuaternion = new Quaternion();
		}
		super.setMaxDigits(2);
		super.setScrollFactor(0.2f);
	}

	@Override
	protected void postValueChanged() {
		mTextFields[0].setFloat(mQuaternion.getYaw()*MathConst.TO_DEG);
		mTextFields[1].setFloat(mQuaternion.getPitch()*MathConst.TO_DEG);
		mTextFields[2].setFloat(mQuaternion.getRoll()*MathConst.TO_DEG);
	}

	@Override
	public void refreshOutValue() {
		mQuaternion.setFromEuler(
				mTextFields[0].getFloat()*MathConst.TO_RAD,
				mTextFields[1].getFloat()*MathConst.TO_RAD,
				mTextFields[2].getFloat()*MathConst.TO_RAD
				);
	}

	@Override
	public Object getValueReference() {
		return mQuaternion;
	}

	@Override
	protected void setValueReference(Object reference) {
		mQuaternion = (Quaternion)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mQuaternion.set((Quaternion)value);
	}

}
