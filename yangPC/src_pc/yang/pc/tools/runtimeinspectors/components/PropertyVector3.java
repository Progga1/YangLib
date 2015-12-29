package yang.pc.tools.runtimeinspectors.components;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;

public class PropertyVector3 extends PropertyNumArrayBase {

	private Point3f mVectorData;

	public PropertyVector3() {
		super(3);
	}

	@Override
	protected void postInit() {
		super.postInit();
		if(!isReferenced()) {
			mVectorData = new Vector3f();
		}
	}

	@Override
	protected void postValueChanged() {
		mTextFields[0].setFloat(mVectorData.mX);
		mTextFields[1].setFloat(mVectorData.mY);
		mTextFields[2].setFloat(mVectorData.mZ);
	}

	@Override
	public void refreshOutValue() {
		mVectorData.set(
				mTextFields[0].getFloat(),
				mTextFields[1].getFloat(),
				mTextFields[2].getFloat()
				);
	}

	@Override
	public Object getValueReference() {
		return mVectorData;
	}

	@Override
	protected void setValueReference(Object reference) {
		mVectorData = (Point3f)reference;
	}

	@Override
	public void setValueFrom(Object value) {
		mVectorData.set((Point3f)value);
	}

}
