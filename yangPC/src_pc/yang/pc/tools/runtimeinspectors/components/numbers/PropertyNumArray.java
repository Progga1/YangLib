package yang.pc.tools.runtimeinspectors.components.numbers;

public class PropertyNumArray extends PropertyNumArrayBase {

	private float[] mFloatValues;
	private double[] mDoubleValues;
	private boolean mDoubleMode = false;
	protected int mStride = 0;

	public PropertyNumArray(int elemCount) {
		super(elemCount);
	}

	@Override
	public void setPreferredOutputType(Class<?> type) {
		mDoubleMode = type==Double.class;
	}

	public PropertyNumArray setMaxColumns(int maxColumns) {
		mMaxColumns = maxColumns;
		return this;
	}

	@Override
	public void refreshOutValue() {
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mElemCount];
			if(mStride>0) {
				int c = 0;
				for(int i=0;i<mElemCount;i++) {
					mDoubleValues[c++] = mTextFields[i].getDouble();
					if((i+1)%mMaxColumns==0)
						c += mStride;
				}
			}else{
				for(int i=0;i<mElemCount;i++) {
					mDoubleValues[i] = mTextFields[i].getDouble();
				}
			}
		}else{
			if(mFloatValues==null)
				mFloatValues = new float[mElemCount];
			if(mStride>0) {
				int c = 0;
				for(int i=0;i<mElemCount;i++) {
					mFloatValues[c++] = mTextFields[i].getFloat();
					if((i+1)%mMaxColumns==0)
						c += mStride;
				}
			}else{
				for(int i=0;i<mElemCount;i++) {
					mFloatValues[i] = mTextFields[i].getFloat();
				}
			}
		}
	}

	@Override
	public void setValueReference(Object reference) {
		if(reference instanceof float[]) {
			mDoubleMode = false;
			mFloatValues = (float[])reference;
		}else if(reference instanceof double[]) {
			mDoubleMode = true;
			mDoubleValues = (double[])reference;
		}else
			throw new RuntimeException("Reference type not supported: "+reference.getClass().getName());
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
	protected void refreshInValue() {
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mElemCount];
			if(mStride>0) {
				int c = 0;
				for(int i=0;i<mElemCount;i++) {
					mTextFields[i].setDouble(mDoubleValues[c++]);
					if((i+1)%mMaxColumns==0)
						c += mStride;
				}
			}else{
				for(int i=0;i<mElemCount;i++) {
					mTextFields[i].setDouble(mDoubleValues[i]);
				}
			}
		}else {
			if(mFloatValues==null)
				mFloatValues = new float[mElemCount];
			if(mStride>0) {
				int c = 0;
				for(int i=0;i<mElemCount;i++) {
					mTextFields[i].setFloat(mFloatValues[c++]);
					if((i+1)%mMaxColumns==0)
						c += mStride;
				}
			}else{
				for(int i=0;i<mElemCount;i++) {
					mTextFields[i].setFloat(mFloatValues[i]);
				}
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

	@Override
	public PropertyNumArray clone() {
		PropertyNumArray newInst = new PropertyNumArray(mElemCount);
		newInst.mDoubleMode = mDoubleMode;
		newInst.mMaxColumns = mMaxColumns;
		newInst.mStride = mStride;
		return newInst;
	}

}
