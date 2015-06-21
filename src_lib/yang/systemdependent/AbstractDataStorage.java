package yang.systemdependent;

public abstract class AbstractDataStorage {

	private static final String FILENAME = "storage.str";
	protected String mFilename;

	public AbstractDataStorage() {
		this(FILENAME);
	}

	public AbstractDataStorage(String name) {
		mFilename = name;
	}


	public abstract void putFloat(String key, float value);
	public abstract void putInt(String key, int value);
	public abstract void putBoolean(String key, boolean value);
	public abstract void putString(String key, String value);
	public abstract void putLong(String key, long value);

	public abstract float getFloat(String key, float defaultValue);
	public abstract int getInt(String key, int defaultValue);
	public abstract boolean getBoolean(String key, boolean defaultValue);
	public abstract String getString(String key, String defaultValue);
	public abstract long getLong(String key, long defaultValue);
}
