package yang.pc.tools.runtimeinspectors.components;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;

public class PropertyVector3 extends PropertyNumArrayBase {

	private Vector3f mVectorData = new Vector3f();

	public PropertyVector3(float defaultVal) {
		super(3,defaultVal);
	}

	public PropertyVector3() {
		super(3);
	}

	@Override
	protected void postSetValue(Object value) {
		Point3f pnt = (Point3f)value;
		mTextFields[0].setFloat(pnt.mX);
		mTextFields[1].setFloat(pnt.mY);
		mTextFields[2].setFloat(pnt.mZ);
	}

	@Override
	protected Object getValue() {

		mVectorData.set(
				mTextFields[0].getFloat((float)mDefaultVals[0]),
				mTextFields[1].getFloat((float)mDefaultVals[1]),
				mTextFields[2].getFloat((float)mDefaultVals[2])
				);
		return mVectorData;
	}

}
