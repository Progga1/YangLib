package yang.model.wrappers;

public class IntWrapper implements IntInterface {

	public int mValue;

	public IntWrapper(int value) {
		mValue = value;
	}

	public IntWrapper() {
		this(0);
	}

	@Override
	public int getInt() {
		return mValue;
	}

	@Override
	public void setInt(int value) {
		mValue = value;
	}

}
