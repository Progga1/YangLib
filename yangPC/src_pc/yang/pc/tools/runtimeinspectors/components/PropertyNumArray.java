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
	public void refreshOutValue() {
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
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mElemCount];
			return mDoubleValues;
		}else{
			if(mFloatValues==null)
				mFloatValues = new float[mElemCount];
			return mFloatValues;
		}
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

	@Override
	public float getFloat(int index) {
		return mTextFields[index].getFloat();
	}

	@Override
	public void setFloat(int index,float value) {
		if(mFloatValues==null)
			mFloatValues = new float[mElemCount];
		mFloatValues[index] = value;
		mTextFields[index].setFloat(value);
	}

	@Override
	public double getDouble(int index) {
		if(mDoubleValues==null)
			mDoubleValues = new double[mElemCount];
		return mDoubleValues[index];
	}

	@Override
	public void setDouble(int index,double value) {
		if(mDoubleValues==null)
			mDoubleValues = new double[mElemCount];
		mDoubleValues[index] = value;
		mTextFields[index].setDouble(value);
	}

}
