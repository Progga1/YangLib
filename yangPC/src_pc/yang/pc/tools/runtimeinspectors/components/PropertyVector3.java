package yang.pc.tools.runtimeinspectors.components;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;

public class PropertyVector3 extends PropertyNumArrayBase {

	private Vector3f mVectorData = new Vector3f();

	public PropertyVector3(float defaultVal) {
		super(3,defaultVal);
		mValueRef = mVectorData;
	}

	public PropertyVector3() {
		super(3);
		mValueRef = mVectorData;
	}

	@Override
	protected void postValueChanged() {
		mTextFields[0].setFloat(mVectorData.mX);
		mTextFields[1].setFloat(mVectorData.mY);
		mTextFields[2].setFloat(mVectorData.mZ);
	}

	@Override
	public void refreshValue() {
		mVectorData.set(
				mTextFields[0].getFloat((float)mDefaultVals[0]),
				mTextFields[1].getFloat((float)mDefaultVals[1]),
				mTextFields[2].getFloat((float)mDefaultVals[2])
				);
	}

	@Override
	public Object getValueReference() {
		return mVectorData;
	}

	@Override
	public void setValueFrom(Object value) {
		mVectorData.set((Point3f)value);
	}

}
