package yang.pc.tools.runtimeinspectors.components;

public class PropertyNumArray extends PropertyNumArrayBase {

	private float[] mFloatValues;
	private double[] mDoubleValues;
	private boolean mDoubleMode = false;

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
				mDoubleValues = new double[mElemCount];
			for(int i=0;i<mDoubleValues.length;i++) {
				mDoubleValues[i] = mTextFields[i].getDouble();
			}

		}else{
			if(mFloatValues==null)
				mFloatValues = new float[mElemCount];
			for(int i=0;i<mFloatValues.length;i++) {
				mFloatValues[i] = mTextFields[i].getFloat();
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
				mDoubleValues = new double[mElemCount];
			for(int i=0;i<mElemCount;i++) {
				mTextFields[i].setDouble(mDoubleValues[i]);
			}
		}else {
			if(mFloatValues==null)
				mFloatValues = new float[mElemCount];
			for(int i=0;i<mElemCount;i++) {
				mTextFields[i].setFloat(mFloatValues[i]);
			}
		}
	}

}
