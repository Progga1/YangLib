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

	@Override
	protected void postSetValue(Object value) {
		if(value instanceof float[]) {
			mDoubleMode = false;
			float[] vals = (float[])value;
			for(int i=0;i<mDefaultVals.length;i++) {
				mTextFields[i].setFloat(vals[i]);
			}
		}else if(value instanceof double[]) {
			mDoubleMode = true;
			double[] vals = (double[])value;
			for(int i=0;i<mDefaultVals.length;i++) {
				mTextFields[i].setDouble(vals[i]);
			}
		}else
			throw new RuntimeException("Invalid array type");
	}

	@Override
	protected Object getValue() {
		if(mDoubleMode) {
			if(mDoubleValues==null)
				mDoubleValues = new double[mDefaultVals.length];
			for(int i=0;i<mDoubleValues.length;i++) {
				mDoubleValues[i] = mTextFields[i].getDouble(mDefaultVals[i]);
			}
			return mDoubleValues;
		}else{
			if(mFloatValues==null)
				mFloatValues = new float[mDefaultVals.length];
			for(int i=0;i<mFloatValues.length;i++) {
				mFloatValues[i] = mTextFields[i].getFloat((float)mDefaultVals[i]);
			}
			return mFloatValues;
		}

	}

}
