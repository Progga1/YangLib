package yang.util.filereader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import yang.systemdependent.AbstractDataStorage;

public class ByteDataStorage extends AbstractDataStorage {

	private Properties mProps;

	public void load(byte[] data) {
		mProps = new Properties();
		try {
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			mProps.load(stream);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reset() {
		mProps = new Properties();
	}

	public byte[] save() {
		byte[] data = null;
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			mProps.store(stream, null);
			data = stream.toByteArray();
			stream.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void putFloat(String key, float value) {
		mProps.put(key, ""+value);
	}

	@Override
	public void putInt(String key, int value) {
		mProps.put(key, ""+value);
	}

	@Override
	public void putLong(String key, long value) {
		mProps.put(key, ""+value);
	}

	@Override
	public void putBoolean(String key, boolean value) {
		mProps.put(key, ""+value);
	}

	@Override
	public void putString(String key, String value) {
		mProps.put(key, ""+value);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		try {
			return Float.parseFloat(mProps.getProperty(key));
		} catch (Exception e) {}
		return defaultValue;
	}

	@Override
	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(mProps.getProperty(key));
		} catch (Exception e) {	}
		return defaultValue;
	}

	@Override
	public long getLong(String key, long defaultValue) {
		try {
			return Long.parseLong(mProps.getProperty(key));
		} catch (Exception e) { }
		return defaultValue;
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		String prop = mProps.getProperty(key);
		if (prop != null && prop.equalsIgnoreCase("false")) return false;
		else if (prop != null && prop.equalsIgnoreCase("true")) return true;
		else return defaultValue;
	}

	@Override
	public String getString(String key, String defaultValue) {
		try {
			String value = mProps.getProperty(key);
			//need a null check here to deliver defaultValue if not existing
			if (value != null) return value;
		} catch (Exception e) {}
		return defaultValue;
	}
}
