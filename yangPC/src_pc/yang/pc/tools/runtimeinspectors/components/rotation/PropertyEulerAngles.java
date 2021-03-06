package yang.pc.tools.runtimeinspectors.components.rotation;

import yang.math.MathConst;
import yang.math.objects.EulerAngles;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArrayBase;
import yang.pc.tools.runtimeinspectors.subcomponents.NumTextField;

public class PropertyEulerAngles extends PropertyNumArrayBase {

	protected final static float CLAMP_PITCH_RANGE = 89.95f;
	protected EulerAngles mEulerAngles;

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
		setMaxDigits(2);
		setScrollFactor(0.35f);
		mTextFieldYaw.setScrollFactor(0.5f);
		mTextFieldYaw.setRange(0,360);
		mTextFieldYaw.setCyclic(true);
		setClampPitch(false);
		mTextFieldRoll.setRange(0,360);
		mTextFieldRoll.setCyclic(true);
	}

	public PropertyEulerAngles setClampPitch(boolean clampPitch) {
		if(clampPitch) {
			mTextFieldPitch.setRange(-CLAMP_PITCH_RANGE,CLAMP_PITCH_RANGE);
			mTextFieldPitch.setCyclic(false);
		}else{
			mTextFieldPitch.setRange(-180,180);
			mTextFieldPitch.setCyclic(true);
		}
		return this;
	}

	@Override
	public void setPreferredOutputType(Class<?> type) {

	}

	@Override
	protected void refreshInValue() {
		float yaw = mEulerAngles.mYaw*MathConst.TO_DEG;
		float pitch = mEulerAngles.mPitch*MathConst.TO_DEG;
		float roll = mEulerAngles.mRoll*MathConst.TO_DEG;
		mTextFields[0].setFloat(yaw);
		mTextFields[1].setFloat(pitch);
		mTextFields[2].setFloat(roll);
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
	public void setValueReference(Object reference) {
		mEulerAngles = (EulerAngles)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mEulerAngles.set((EulerAngles)value);
	}

}
