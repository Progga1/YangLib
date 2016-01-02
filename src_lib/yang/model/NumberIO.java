package yang.model;

public interface NumberIO {

	public void setDouble(double value);
	public double getDouble();

	public void setMinValue(double minValue);
	public void setMaxValue(double maxValue);
	public void setDefaultValue(double defaultValue);
	public void setScrollFactor(float stepsPerPixel);

}
