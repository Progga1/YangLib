package yang.systemdependent;

public abstract class AbstractDataStorage {

	protected static final String FILENAME = "storage";

	public abstract void putFloat(String key, float value);
	public abstract void putInt(String key, int value);
	public abstract void putBoolean(String key, boolean value);
	public abstract void putString(String key, String value);

	public abstract float getFloat(String key, float defaultValue);
	public abstract int getInt(String key, int defaultValue);
	public abstract boolean getBoolean(String key, boolean defaultValue);
	public abstract String getString(String key, String defaultValue);
}
