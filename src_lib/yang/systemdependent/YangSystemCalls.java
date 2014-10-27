package yang.systemdependent;

import yang.model.DebugYang;

public class YangSystemCalls {

	public static boolean ALWAYS_RELOAD_AFTER_PAUSE = false;

	public void openKeyBoard() {

	}

	public boolean reloadAfterPause() {
		return true;
	}

	public void throwDebugIntent() {

	}

	public void hideKeyBoard() {

	}

	public void exit() {

	}

	public String getSystemLanguage() {
		if (DebugYang.systemLanguage != null) return DebugYang.systemLanguage;
		return "en";
	}
}
