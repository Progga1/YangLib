package yang.systemdependent;

import java.util.Locale;

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
		/*
		Locale.getDefault().getLanguage()       ---> en
		Locale.getDefault().getISO3Language()   ---> eng
		Locale.getDefault().getCountry()        ---> US
		Locale.getDefault().getISO3Country()    ---> USA
		Locale.getDefault().getDisplayCountry() ---> United States
		Locale.getDefault().getDisplayName()    ---> English (United States)
		 */
		return Locale.getDefault().getLanguage();
	}

	public String getPlatfrom() {
		return "";
	}
}
