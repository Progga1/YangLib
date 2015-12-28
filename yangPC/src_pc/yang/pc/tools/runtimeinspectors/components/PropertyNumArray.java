package yang.pc.tools.runtimeinspectors.components;

public class PropertyNumArray extends PropertyNumArrayBase {

	private float[] mFloatValues;
	private double[] mDoubleValues;
	private boolean mDoubleMode = false;

	public PropertyNumArray(double[] defaultVals) {
		super(defaultVals);
	}

	public PropertyNumArray(int elemCount,double defaultVal) {
		super(elemCount,defaultVal);
	}

	public PropertyNumArray(int elemCount) {
		super(elemCount);
	}

	public PropertyNumArray setDoubleMode() {
		mDoubleMode = true;
		return this;
	}

	@Override
	public void refreshValue() {
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mDefaultVals.length];
			for(int i=0;i<mDoubleValues.length;i++) {
				mDoubleValues[i] = mTextFields[i].getDouble(mDefaultVals[i]);
			}

		}else{
			if(mFloatValues==null)
				mFloatValues = new float[mDefaultVals.length];
			for(int i=0;i<mFloatValues.length;i++) {
				mFloatValues[i] = mTextFields[i].getFloat((float)mDefaultVals[i]);
			}
		}
	}

	@Override
	public Object getValueReference() {
		if(mDoubleMode)
			return mDoubleValues;
		else
			return mFloatValues;
	}

	@Override
	protected void postValueChanged() {
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mDefaultVals.length];
			mValueRef = mDoubleValues;
			for(int i=0;i<mDefaultVals.length;i++) {
				mTextFields[i].setDouble(mDoubleValues[i]);
			}
		}else {
			if(mFloatValues==null)
				mFloatValues = new float[mDefaultVals.length];
			mValueRef = mFloatValues;
			for(int i=0;i<mDefaultVals.length;i++) {
				mTextFields[i].setFloat(mFloatValues[i]);
			}
		}
	}

}
