package yang.gdx.fileio;

import yang.systemdependent.AbstractDataStorage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class GdxDataStorage extends AbstractDataStorage {

	@Override
	public void putFloat(String key, float value) {
		Preferences prefs = Gdx.app.getPreferences(FILENAME);
		prefs.putFloat(key, value);
		prefs.flush();
	}

	@Override
	public void putInt(String key, int value) {
		Preferences prefs = Gdx.app.getPreferences(FILENAME);
		prefs.putInteger(key, value);
		prefs.flush();
	}

	@Override
	public void putBoolean(String key, boolean value) {
		Preferences prefs = Gdx.app.getPreferences(FILENAME);
		prefs.putBoolean(key, value);
		prefs.flush();
	}

	@Override
	public float getFloat(String key, float defaultValue) {
		return Gdx.app.getPreferences(FILENAME).getFloat(key, defaultValue);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return Gdx.app.getPreferences(FILENAME).getInteger(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return Gdx.app.getPreferences(FILENAME).getBoolean(key, defaultValue);
	}

}
