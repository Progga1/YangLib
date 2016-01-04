package yang.model.wrappers;

public class FloatWrapper implements FloatInterface {

	public float mValue;

	public FloatWrapper(float value) {
		mValue = value;
	}

	public FloatWrapper() {
		this(0);
	}

	@Override
	public float getFloat() {
		return mValue;
	}

	@Override
	public void setFloat(float value) {
		mValue = value;
	}

}
