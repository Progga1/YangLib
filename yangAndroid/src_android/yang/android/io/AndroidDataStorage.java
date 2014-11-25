package yang.android.io;

import yang.systemdependent.AbstractDataStorage;
import android.content.Context;
import android.content.SharedPreferences.Editor;

public class AndroidDataStorage extends AbstractDataStorage {

	private Context mContext;

	public AndroidDataStorage(Context context) {
		mContext = context;
	}

	@Override
	public void putFloat(String key, float value) {
		Editor prefs = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
		prefs.putFloat(key, value);
		prefs.commit();
	}

	@Override
	public void putInt(String key, int value) {
		Editor prefs = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
		prefs.putInt(key, value);
		prefs.commit();
	}

	@Override
	public void putLong(String key, long value) {
		Editor prefs = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
		prefs.putLong(key, value);
		prefs.commit();
	}

	@Override
	public void putBoolean(String key, boolean value) {
		Editor prefs = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
		prefs.putBoolean(key, value);
		prefs.commit();
	}

	@Override
	public void putString(String key, String value) {
		Editor prefs = mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
		prefs.putString(key, value);
		prefs.commit();
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		return mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getFloat(key, defaultValue);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getInt(key, defaultValue);
	}

	@Override
	public long getLong(String key, long defaultValue) {
		return mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getLong(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getBoolean(key, defaultValue);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return mContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).getString(key, defaultValue);
	}
}
