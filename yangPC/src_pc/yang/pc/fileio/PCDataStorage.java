package yang.pc.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import yang.systemdependent.AbstractDataStorage;


public class PCDataStorage extends AbstractDataStorage {

	public PCDataStorage(String name) {
		super(name);
	}

	public PCDataStorage() {
		super();
	}

	private Properties load() {
		Properties props = new Properties();
		try {

			File f = new File(mFilename);
			if (!f.exists()) {
				System.out.println("didnt exist, created");
				f.createNewFile();
			}

			FileInputStream fis = new FileInputStream(mFilename);
			props.load(fis);
			fis.close();
		} catch (Exception e) {
			System.err.println("failed to read '"+mFilename+"'");
			System.exit(0);
		}

		return props;
	}

	private void save(Properties props) {
		try {
			FileOutputStream fos = new FileOutputStream(mFilename);
			props.store(fos, null);
			fos.close();
		} catch (Exception e) {
			System.err.println("failed to write '"+mFilename+"'");
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void putFloat(String key, float value) {
		Properties props = load();
		props.put(key, ""+value);
		save(props);
	}

	@Override
	public void putInt(String key, int value) {
		Properties props = load();
		props.put(key, ""+value);
		save(props);
	}

	@Override
	public void putLong(String key, long value) {
		Properties props = load();
		props.put(key, ""+value);
		save(props);
	}

	@Override
	public void putBoolean(String key, boolean value) {
		Properties props = load();
		props.put(key, ""+value);
		save(props);
	}

	@Override
	public void putString(String key, String value) {
		Properties props = load();
		props.put(key, value);
		save(props);
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		Properties props = load();
		try {
			return Float.parseFloat(props.getProperty(key));
		} catch (Exception e) {}
		return defaultValue;
	}

	@Override
	public int getInt(String key, int defaultValue) {
		Properties props = load();
		try {
			return Integer.parseInt(props.getProperty(key));
		} catch (Exception e) { }
		return defaultValue;
	}

	@Override
	public long getLong(String key, long defaultValue) {
		Properties props = load();
		try {
			return Long.parseLong(props.getProperty(key));
		} catch (Exception e) { }
		return defaultValue;
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		Properties props = load();
		String prop = props.getProperty(key);
		if (prop != null && prop.equalsIgnoreCase("false")) return false;
		else if (prop != null && prop.equalsIgnoreCase("true")) return true;
		else return defaultValue;
	}

	@Override
	public String getString(String key, String defaultValue) {
		Properties props = load();
		try {
			String value = props.getProperty(key);
			//need a null check here to deliver defaultValue if not existing
			if (value != null) return value;
		} catch (Exception e) {}
		return defaultValue;
	}
}
