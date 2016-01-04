package yang.model.wrappers;

public class DoubleWrapper implements DoubleInterface {

	public double mValue;

	public DoubleWrapper(double value) {
		mValue = value;
	}

	public DoubleWrapper() {
		this(0);
	}

	@Override
	public double getDouble() {
		return mValue;
	}

	@Override
	public void setDouble(double value) {
		mValue = value;
	}

}
