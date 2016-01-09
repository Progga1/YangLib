package yang.pc.tools.runtimeinspectors.components;

import yang.model.Boundaries3D;
import yang.pc.tools.runtimeinspectors.InspectorComponent;
import yang.pc.tools.runtimeinspectors.PropertyChain;
import yang.pc.tools.runtimeinspectors.components.numbers.PropertyNumArray;

public class PropertyBoundaries3D extends PropertyChain {

	private Boundaries3D mBoundaries;
	private PropertyNumArray mTextFieldsX;
	private PropertyNumArray mTextFieldsY;
	private PropertyNumArray mTextFieldsZ;

	@Override
	protected void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mBoundaries = new Boundaries3D();
		}
	}

	@Override
	protected InspectorComponent[] createComponents() {
		mTextFieldsX = new PropertyNumArray(2);
		mTextFieldsX.init(this, "X (min max)", true);
		mTextFieldsY = new PropertyNumArray(2);
		mTextFieldsY.init(this, "Y (min max)", true);
		mTextFieldsZ = new PropertyNumArray(2);
		mTextFieldsZ.init(this, "Z (min max)", true);
		return new InspectorComponent[]{mTextFieldsX,mTextFieldsY,mTextFieldsZ};
	}

	@Override
	protected void refreshInValue() {
		mTextFieldsX.setFloat(0,mBoundaries.mMinX);
		mTextFieldsX.setFloat(1,mBoundaries.mMaxX);
		mTextFieldsY.setFloat(0,mBoundaries.mMinY);
		mTextFieldsY.setFloat(1,mBoundaries.mMaxY);
		mTextFieldsZ.setFloat(0,mBoundaries.mMinZ);
		mTextFieldsZ.setFloat(1,mBoundaries.mMaxZ);
	}

	@Override
	protected void refreshOutValue() {
		super.refreshOutValue();
		mBoundaries.mMinX = mTextFieldsX.getFloat(0);
		mBoundaries.mMaxX = mTextFieldsX.getFloat(1);
		mBoundaries.mMinY = mTextFieldsY.getFloat(0);
		mBoundaries.mMaxY = mTextFieldsY.getFloat(1);
		mBoundaries.mMinZ = mTextFieldsZ.getFloat(0);
		mBoundaries.mMaxZ = mTextFieldsZ.getFloat(1);
	}

	@Override
	public Object getValueReference() {
		return mBoundaries;
	}

	@Override
	public void setValueReference(Object reference) {
		mBoundaries = (Boundaries3D)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mBoundaries.set((Boundaries3D)value);
	}

}
