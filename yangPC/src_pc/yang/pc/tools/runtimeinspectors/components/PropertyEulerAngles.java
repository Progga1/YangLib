package yang.pc.tools.runtimeinspectors.components;

import yang.math.MathConst;
import yang.math.objects.EulerAngles;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyEulerAngles extends PropertyNumArrayBase {

	private final static float PITCH_RANGE = 89.5f;
	private EulerAngles mEulerAngles;

	private NumTextField mTextFieldYaw,mTextFieldPitch,mTextFieldRoll;

	public PropertyEulerAngles() {
		super(3);
	}

	@Override
	protected void postInit() {
		super.postInit();
		mTextFieldYaw = mTextFields[0];
		mTextFieldPitch = mTextFields[1];
		mTextFieldRoll = mTextFields[2];
		if(!isReferenced()) {
			mEulerAngles = new EulerAngles();
		}
		mTextFields[1].setRange(-PITCH_RANGE,PITCH_RANGE);
		setMaxDigits(2);
		setScrollFactor(0.35f);
		mTextFieldYaw.setScrollFactor(0.5f);
		mTextFieldYaw.setRange(0,360);
		mTextFieldYaw.setCyclic(true);
		mTextFieldRoll.setRange(0,360);
		mTextFieldRoll.setCyclic(true);
	}

	@Override
	protected void postValueChanged() {
		mTextFields[0].setFloat(mEulerAngles.mYaw*MathConst.TO_DEG);
		mTextFields[1].setFloat(mEulerAngles.mPitch*MathConst.TO_DEG);
		mTextFields[2].setFloat(mEulerAngles.mRoll*MathConst.TO_DEG);
	}

	@Override
	public void refreshOutValue() {
		mEulerAngles.set(
				mTextFields[0].getFloat()*MathConst.TO_RAD,
				mTextFields[1].getFloat()*MathConst.TO_RAD,
				mTextFields[2].getFloat()*MathConst.TO_RAD
				);
	}

	@Override
	public Object getValueReference() {
		return mEulerAngles;
	}

	@Override
	protected void setValueReference(Object reference) {
		mEulerAngles = (EulerAngles)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mEulerAngles.set((EulerAngles)value);
	}

}
