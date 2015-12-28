package yang.pc.tools.runtimeinspectors.components;

import yang.graphics.model.FloatColor;

public class PropertyColorNums extends PropertyNumArrayBase {

	private FloatColor mColor;

	public PropertyColorNums() {
		super(4,1);
	}

	@Override
	public void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mColor = new FloatColor(mDefaultVals);
			mValueRef = mColor;
		}
	}

	@Override
	protected void postValueChanged() {
		mTextFields[0].setFloat(mColor.mValues[0]);
		mTextFields[1].setFloat(mColor.mValues[1]);
		mTextFields[2].setFloat(mColor.mValues[2]);
		mTextFields[3].setFloat(mColor.mValues[3]);
	}

	@Override
	public void refreshValue() {
		mColor.set(
				mTextFields[0].getFloat((float)mDefaultVals[0]),
				mTextFields[1].getFloat((float)mDefaultVals[1]),
				mTextFields[2].getFloat((float)mDefaultVals[2]),
				mTextFields[3].getFloat((float)mDefaultVals[2])
				);

	}

	@Override
	public Object getValueReference() {
		return mColor;
	}

	@Override
	public void setValueFrom(Object value) {
		mColor.set((FloatColor)value);
	}

	@Override
	protected void setValueReference(Object reference) {
		mColor = (FloatColor)reference;
	}

}
